param(
    [string] $RawDataDir,
    [string] $OutputFile
)

$ErrorActionPreference = "Stop"

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path

if ([string]::IsNullOrWhiteSpace($RawDataDir)) {
    $RawDataDir = Join-Path $repoRoot "app/src/main/java/de/impulse/spieleabend/data/seed/rohdaten"
}

if ([string]::IsNullOrWhiteSpace($OutputFile)) {
    $OutputFile = Join-Path $repoRoot "app/src/main/java/de/impulse/spieleabend/data/seed/InitialGameData.kt"
}

$utf8Strict = New-Object System.Text.UTF8Encoding -ArgumentList $false, $true
$utf8NoBom = New-Object System.Text.UTF8Encoding -ArgumentList $false
$ignoredGameNames = @("Privacy")

function ConvertTo-Slug {
    param([Parameter(Mandatory = $true)][string] $Value)

    $text = $Value.Trim().ToLowerInvariant()
    $text = $text.Replace([string][char]0x00E4, "ae")
    $text = $text.Replace([string][char]0x00F6, "oe")
    $text = $text.Replace([string][char]0x00FC, "ue")
    $text = $text.Replace([string][char]0x00DF, "ss")
    $text = $text.Replace("'", "")
    $text = $text.Replace([string][char]0x2019, "")
    $text = $text.Normalize([System.Text.NormalizationForm]::FormD)

    $builder = New-Object System.Text.StringBuilder
    foreach ($char in $text.ToCharArray()) {
        $category = [System.Globalization.CharUnicodeInfo]::GetUnicodeCategory($char)
        if ($category -eq [System.Globalization.UnicodeCategory]::NonSpacingMark) {
            continue
        }

        if ([char]::IsLetterOrDigit($char)) {
            [void] $builder.Append($char)
        } else {
            [void] $builder.Append("-")
        }
    }

    $slug = [regex]::Replace($builder.ToString(), "-+", "-").Trim("-")
    if ([string]::IsNullOrWhiteSpace($slug)) {
        return "id"
    }

    return $slug
}

function ConvertTo-KotlinStringLiteral {
    param([AllowEmptyString()][string] $Value)

    $builder = New-Object System.Text.StringBuilder
    [void] $builder.Append('"')

    foreach ($char in $Value.ToCharArray()) {
        $code = [int][char]$char

        if ($char -eq [char]34) {
            [void] $builder.Append('\"')
        } elseif ($char -eq [char]36) {
            [void] $builder.Append('\$')
        } elseif ($char -eq [char]92) {
            [void] $builder.Append('\\')
        } elseif ($char -eq [char]10) {
            [void] $builder.Append('\n')
        } elseif ($char -eq [char]13) {
            [void] $builder.Append('\r')
        } elseif ($char -eq [char]9) {
            [void] $builder.Append('\t')
        } elseif ($code -ge 32 -and $code -le 126) {
            [void] $builder.Append($char)
        } else {
            [void] $builder.Append("\u$($code.ToString("X4"))")
        }
    }

    [void] $builder.Append('"')
    return $builder.ToString()
}

function Read-RawCardTexts {
    param([Parameter(Mandatory = $true)][string] $Path)

    $content = [System.IO.File]::ReadAllText($Path, $utf8Strict).Trim()
    if ([string]::IsNullOrWhiteSpace($content)) {
        return @()
    }

    $content = [regex]::Replace($content, ",\s*$", "")
    $json = "[$content]"

    try {
        return @($json | ConvertFrom-Json)
    } catch {
        throw "Could not parse raw text file '$Path' as a JSON string list. $($_.Exception.Message)"
    }
}

function Split-RawFileName {
    param([Parameter(Mandatory = $true)][System.IO.FileInfo] $File)

    $fileName = [System.IO.Path]::GetFileNameWithoutExtension($File.Name)
    if ($fileName -notmatch "^(?<category>.+)_(?<language>[A-Za-z]{2,10})$") {
        throw "Raw file '$($File.FullName)' must end with _<language>.txt."
    }

    return [pscustomobject]@{
        CategoryName = $Matches.category.Trim()
        LanguageCode = $Matches.language.Trim().ToLowerInvariant()
    }
}

function Get-LanguagePriority {
    param([Parameter(Mandatory = $true)][string] $LanguageCode)

    switch ($LanguageCode) {
        "de" { return 0 }
        "en" { return 1 }
        default { return 2 }
    }
}

function Sort-LanguageCodes {
    param([Parameter(Mandatory = $true)][string[]] $LanguageCodes)

    return @(
        $LanguageCodes |
            Sort-Object @{ Expression = { Get-LanguagePriority $_ } }, @{ Expression = { $_ } }
    )
}

function Add-Line {
    param(
        [System.Collections.Generic.List[string]] $Lines,
        [AllowEmptyString()][string] $Line = ""
    )

    $Lines.Add($Line)
}

if (-not (Test-Path $RawDataDir -PathType Container)) {
    throw "Raw data directory '$RawDataDir' does not exist."
}

$gameDirs = @(
    Get-ChildItem -Path $RawDataDir -Directory |
        Where-Object { $ignoredGameNames -notcontains $_.Name } |
        Sort-Object Name
)

if ($gameDirs.Count -eq 0) {
    throw "No game folders found in '$RawDataDir'."
}

$usedGameIds = @{}
$games = @()

foreach ($gameDir in $gameDirs) {
    $gameId = ConvertTo-Slug $gameDir.Name
    if ($usedGameIds.ContainsKey($gameId)) {
        throw "Duplicate game id '$gameId' generated from '$($gameDir.Name)'."
    }
    $usedGameIds[$gameId] = $true

    $rawFiles = @(Get-ChildItem -Path $gameDir.FullName -File -Filter "*.txt" | Sort-Object Name)
    if ($rawFiles.Count -eq 0) {
        throw "Game folder '$($gameDir.FullName)' does not contain raw text files."
    }

    $categoryMap = [ordered]@{}
    foreach ($rawFile in $rawFiles) {
        $fileInfo = Split-RawFileName $rawFile
        if (-not $categoryMap.Contains($fileInfo.CategoryName)) {
            $categoryMap[$fileInfo.CategoryName] = [pscustomobject]@{
                Name = $fileInfo.CategoryName
                Languages = [ordered]@{}
            }
        }

        $category = $categoryMap[$fileInfo.CategoryName]
        if ($category.Languages.Contains($fileInfo.LanguageCode)) {
            throw "Duplicate language '$($fileInfo.LanguageCode)' for category '$($fileInfo.CategoryName)' in '$($gameDir.Name)'."
        }

        $category.Languages[$fileInfo.LanguageCode] = Read-RawCardTexts $rawFile.FullName
    }

    $usedCategoryIds = @{}
    $categories = @()
    foreach ($categoryName in $categoryMap.Keys) {
        $category = $categoryMap[$categoryName]
        $categoryId = "$gameId-$(ConvertTo-Slug $category.Name)"
        if ($usedCategoryIds.ContainsKey($categoryId)) {
            throw "Duplicate category id '$categoryId' generated in game '$($gameDir.Name)'."
        }
        $usedCategoryIds[$categoryId] = $true

        $languages = Sort-LanguageCodes @($category.Languages.Keys)
        $counts = @($languages | ForEach-Object { $category.Languages[$_].Count })
        if (@($counts | Select-Object -Unique).Count -ne 1) {
            $countSummary = ($languages | ForEach-Object { "$_=$($category.Languages[$_].Count)" }) -join ", "
            throw "Category '$($category.Name)' in '$($gameDir.Name)' has mismatched translation counts: $countSummary."
        }

        $cardTexts = @()
        for ($index = 0; $index -lt $counts[0]; $index++) {
            $translations = @()
            foreach ($language in $languages) {
                $text = [string]$category.Languages[$language][$index]
                if (-not [string]::IsNullOrWhiteSpace($text)) {
                    $translations += [pscustomobject]@{
                        LanguageCode = $language
                        Text = $text
                    }
                }
            }

            if ($translations.Count -eq 0) {
                $displayIndex = $index + 1
                throw "Category '$($category.Name)' in '$($gameDir.Name)' has no non-empty translation at row $displayIndex."
            }

            $cardTexts += [pscustomobject]@{
                Id = "$categoryId-$("{0:D3}" -f ($index + 1))"
                Translations = $translations
            }
        }

        $categories += [pscustomobject]@{
            Id = $categoryId
            Name = $category.Name
            CardTexts = $cardTexts
        }
    }

    $games += [pscustomobject]@{
        Id = $gameId
        Name = $gameDir.Name
        Categories = $categories
    }
}

$defaultGameId = $games[0].Id
$lines = New-Object "System.Collections.Generic.List[string]"

Add-Line $lines '@file:Suppress("LargeClass", "LongMethod", "MagicNumber", "MaxLineLength")'
Add-Line $lines ""
Add-Line $lines "package de.impulse.spieleabend.data.seed"
Add-Line $lines ""
Add-Line $lines "import de.impulse.spieleabend.domain.model.Kartentext"
Add-Line $lines "import de.impulse.spieleabend.domain.model.Kategorie"
Add-Line $lines "import de.impulse.spieleabend.domain.model.Lokalisierung"
Add-Line $lines "import de.impulse.spieleabend.domain.model.Spiel"
Add-Line $lines "import de.impulse.spieleabend.domain.model.Translation"
Add-Line $lines ""
Add-Line $lines "internal object InitialGameData {"
Add-Line $lines "    const val DEFAULT_GAME_ID = $(ConvertTo-KotlinStringLiteral $defaultGameId)"
Add-Line $lines ""
Add-Line $lines "    val spiele: List<Spiel> = listOf("

foreach ($game in $games) {
    Add-Line $lines "        spiel("
    Add-Line $lines "            id = $(ConvertTo-KotlinStringLiteral $game.Id),"
    Add-Line $lines "            name = $(ConvertTo-KotlinStringLiteral $game.Name),"
    Add-Line $lines "            kartentexteProKarte = 1,"
    Add-Line $lines "            kategorien = linkedSetOf("

    foreach ($category in $game.Categories) {
        Add-Line $lines "                kategorie("
        Add-Line $lines "                    id = $(ConvertTo-KotlinStringLiteral $category.Id),"
        Add-Line $lines "                    name = $(ConvertTo-KotlinStringLiteral $category.Name),"
        Add-Line $lines "                    kartentexte = linkedSetOf("

        foreach ($cardText in $category.CardTexts) {
            Add-Line $lines "                        kartentext("
            Add-Line $lines "                            id = $(ConvertTo-KotlinStringLiteral $cardText.Id),"
            Add-Line $lines "                            translationen = linkedSetOf("

            foreach ($translation in $cardText.Translations) {
                Add-Line $lines "                                Translation("
                Add-Line $lines "                                    sprachCode = $(ConvertTo-KotlinStringLiteral $translation.LanguageCode),"
                Add-Line $lines "                                    text = $(ConvertTo-KotlinStringLiteral $translation.Text),"
                Add-Line $lines "                                ),"
            }

            Add-Line $lines "                            ),"
            Add-Line $lines "                        ),"
        }

        Add-Line $lines "                    ),"
        Add-Line $lines "                ),"
    }

    Add-Line $lines "            ),"
    Add-Line $lines "        ),"
}

Add-Line $lines "    )"
Add-Line $lines ""
Add-Line $lines "    private fun spiel("
Add-Line $lines "        id: String,"
Add-Line $lines "        name: String,"
Add-Line $lines "        kartentexteProKarte: Int,"
Add-Line $lines "        kategorien: Set<Kategorie>,"
Add-Line $lines "    ): Spiel ="
Add-Line $lines "        Spiel("
Add-Line $lines "            id = id,"
Add-Line $lines "            lokalisierung = lokalisierung("
Add-Line $lines "                id = ""`$id-name"","
Add-Line $lines "                translationen = linkedSetOf(Translation(sprachCode = ""de"", text = name)),"
Add-Line $lines "            ),"
Add-Line $lines "            kategorien = kategorien,"
Add-Line $lines "            kartentexteProKarte = kartentexteProKarte,"
Add-Line $lines "        )"
Add-Line $lines ""
Add-Line $lines "    private fun kategorie("
Add-Line $lines "        id: String,"
Add-Line $lines "        name: String,"
Add-Line $lines "        kartentexte: Set<Kartentext>,"
Add-Line $lines "    ): Kategorie ="
Add-Line $lines "        Kategorie("
Add-Line $lines "            id = id,"
Add-Line $lines "            lokalisierung = lokalisierung("
Add-Line $lines "                id = ""`$id-name"","
Add-Line $lines "                translationen = linkedSetOf(Translation(sprachCode = ""de"", text = name)),"
Add-Line $lines "            ),"
Add-Line $lines "            kartentexte = kartentexte,"
Add-Line $lines "        )"
Add-Line $lines ""
Add-Line $lines "    private fun kartentext("
Add-Line $lines "        id: String,"
Add-Line $lines "        translationen: Set<Translation>,"
Add-Line $lines "    ): Kartentext ="
Add-Line $lines "        Kartentext("
Add-Line $lines "            id = id,"
Add-Line $lines "            lokalisierung = lokalisierung("
Add-Line $lines "                id = ""`$id-text"","
Add-Line $lines "                translationen = translationen,"
Add-Line $lines "            ),"
Add-Line $lines "        )"
Add-Line $lines ""
Add-Line $lines "    private fun lokalisierung("
Add-Line $lines "        id: String,"
Add-Line $lines "        translationen: Set<Translation>,"
Add-Line $lines "    ): Lokalisierung ="
Add-Line $lines "        Lokalisierung("
Add-Line $lines "            id = id,"
Add-Line $lines "            translationen = translationen,"
Add-Line $lines "        )"
Add-Line $lines "}"

$outputDirectory = Split-Path -Parent $OutputFile
if (-not (Test-Path $outputDirectory -PathType Container)) {
    New-Item -Path $outputDirectory -ItemType Directory | Out-Null
}

[System.IO.File]::WriteAllText($OutputFile, ($lines -join [Environment]::NewLine) + [Environment]::NewLine, $utf8NoBom)

$categoryCount = @($games | ForEach-Object { $_.Categories }).Count
$cardTextCount = @($games | ForEach-Object { $_.Categories } | ForEach-Object { $_.CardTexts }).Count
$cardTranslationCount = @(
    $games |
        ForEach-Object { $_.Categories } |
        ForEach-Object { $_.CardTexts } |
        ForEach-Object { $_.Translations }
).Count
$translationCount = $cardTranslationCount + $games.Count + $categoryCount

Write-Host "Generated $OutputFile"
Write-Host "Games: $($games.Count), categories: $categoryCount, card texts: $cardTextCount, translations: $translationCount"
Write-Host "Ignored game folders: $($ignoredGameNames -join ', ')"
