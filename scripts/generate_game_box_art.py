from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
from typing import Iterable

from PIL import Image, ImageColor, ImageDraw, ImageFont


ROOT_DIR = Path(__file__).resolve().parents[1]
OUTPUT_DIR = ROOT_DIR / "app" / "src" / "main" / "res" / "drawable-nodpi"
FONT_DIR = Path(r"C:\Windows\Fonts")
TITLE_FONTS = ("arialbd.ttf", "bahnschrift.ttf", "arial.ttf")
BODY_FONTS = ("bahnschrift.ttf", "arial.ttf")


@dataclass(frozen=True)
class ArtSpec:
    filename: str
    size: tuple[int, int]
    title: str
    strapline: str
    base: str
    dark: str
    light: str
    accent: str
    pattern: str


ART_SPECS = (
    ArtSpec(
        filename="game_box_side_erzaehlt_euch_mehr.png",
        size=(1500, 330),
        title="ERZÄHLT\nEUCH MEHR",
        strapline="Fragen für echte Gespräche",
        base="#2F6B5C",
        dark="#173A32",
        light="#F7F1E2",
        accent="#E7C36C",
        pattern="bands",
    ),
    ArtSpec(
        filename="game_box_side_erzaehlt_euch_mehr_fuer_paare.png",
        size=(1220, 360),
        title="ERZÄHLT EUCH MEHR\nFÜR PAARE",
        strapline="Date night edition",
        base="#874A65",
        dark="#482433",
        light="#F8EBDD",
        accent="#F2B27A",
        pattern="hearts",
    ),
    ArtSpec(
        filename="game_box_side_fun_facts.png",
        size=(1420, 270),
        title="FUN FACTS",
        strapline="Schätzen, lachen, überraschen",
        base="#D7773B",
        dark="#6F341A",
        light="#FBE6B9",
        accent="#2A5F79",
        pattern="confetti",
    ),
    ArtSpec(
        filename="game_box_side_privacy.png",
        size=(1080, 320),
        title="PRIVACY",
        strapline="Wie gut kennt ihr euch wirklich?",
        base="#31547F",
        dark="#15263D",
        light="#F2F0E6",
        accent="#D54E43",
        pattern="rings",
    ),
    ArtSpec(
        filename="game_box_side_were_not_really_strangers.png",
        size=(1580, 250),
        title="WE'RE NOT REALLY\nSTRANGERS",
        strapline="Perception. Connection. Reflection.",
        base="#E8E2D7",
        dark="#1B1A1A",
        light="#FFFDF8",
        accent="#D64F43",
        pattern="blocks",
    ),
)


def main() -> None:
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    for spec in ART_SPECS:
        image = create_image(spec)
        image.save(OUTPUT_DIR / spec.filename, optimize=True)


def create_image(spec: ArtSpec) -> Image.Image:
    width, height = spec.size
    image = Image.new("RGBA", (width, height), spec.base)
    draw = ImageDraw.Draw(image)

    paint_vertical_gradient(draw, spec.size, spec.base, spec.dark)
    draw.rounded_rectangle(
        (8, 8, width - 8, height - 8),
        radius=34,
        outline=spec.light,
        width=4,
    )

    draw.rectangle((0, 0, width, height * 0.14), fill=with_alpha(spec.light, 48))
    draw.rectangle((0, height * 0.86, width, height), fill=with_alpha(spec.dark, 110))
    draw.rectangle((0, height * 0.72, width, height * 0.78), fill=with_alpha(spec.accent, 255))

    pattern_area = (int(width * 0.62), 0, width, height)
    draw_pattern(draw, pattern_area, spec)
    draw_label_block(draw, spec)
    draw_title(draw, spec)
    draw_strapline(draw, spec)

    return image


def paint_vertical_gradient(
    draw: ImageDraw.ImageDraw,
    size: tuple[int, int],
    start_hex: str,
    end_hex: str,
) -> None:
    width, height = size
    start = ImageColor.getrgb(start_hex)
    end = ImageColor.getrgb(end_hex)
    for y in range(height):
        mix = y / max(height - 1, 1)
        color = tuple(int(start[i] + (end[i] - start[i]) * mix) for i in range(3))
        draw.line((0, y, width, y), fill=color)


def draw_label_block(draw: ImageDraw.ImageDraw, spec: ArtSpec) -> None:
    width, height = spec.size
    left = int(width * 0.04)
    top = int(height * 0.18)
    right = int(width * 0.16)
    bottom = int(height * 0.82)
    draw.rounded_rectangle(
        (left, top, right, bottom),
        radius=26,
        fill=with_alpha(spec.dark, 212),
    )
    accent_top = top + 18
    draw.rounded_rectangle(
        (left + 18, accent_top, right - 18, accent_top + int(height * 0.12)),
        radius=18,
        fill=spec.accent,
    )
    body_font = load_font(BODY_FONTS, max(int(height * 0.11), 22))
    label = spec.strapline.upper()
    max_width = right - left - 36
    lines = wrap_text(draw, label, body_font, max_width, max_lines=4)
    line_height = body_font.size + 4
    total_height = len(lines) * line_height
    y = bottom - total_height - 28
    for line in lines:
        text_width = draw.textbbox((0, 0), line, font=body_font)[2]
        draw.text(
            (left + (right - left - text_width) / 2, y),
            line,
            font=body_font,
            fill=spec.light,
        )
        y += line_height


def draw_title(draw: ImageDraw.ImageDraw, spec: ArtSpec) -> None:
    width, height = spec.size
    box = (
        int(width * 0.21),
        int(height * 0.18),
        int(width * 0.78),
        int(height * 0.66),
    )
    font, lines = fit_text(draw, spec.title, box, TITLE_FONTS, min_size=44, max_size=int(height * 0.23))
    bbox = draw.multiline_textbbox((0, 0), "\n".join(lines), font=font, spacing=int(font.size * 0.12))
    text_width = bbox[2] - bbox[0]
    text_height = bbox[3] - bbox[1]
    x = box[0] + (box[2] - box[0] - text_width) / 2
    y = box[1] + (box[3] - box[1] - text_height) / 2

    shadow = with_alpha(spec.dark, 180)
    for dx, dy in ((4, 4), (0, 3)):
        draw.multiline_text(
            (x + dx, y + dy),
            "\n".join(lines),
            font=font,
            fill=shadow,
            spacing=int(font.size * 0.12),
            align="center",
        )

    draw.multiline_text(
        (x, y),
        "\n".join(lines),
        font=font,
        fill=spec.light if spec.filename != "game_box_side_were_not_really_strangers.png" else spec.dark,
        spacing=int(font.size * 0.12),
        align="center",
    )


def draw_strapline(draw: ImageDraw.ImageDraw, spec: ArtSpec) -> None:
    width, height = spec.size
    box = (
        int(width * 0.23),
        int(height * 0.76),
        int(width * 0.77),
        int(height * 0.94),
    )
    font, lines = fit_text(
        draw,
        spec.strapline,
        box,
        BODY_FONTS,
        min_size=20,
        max_size=int(height * 0.09),
        max_lines=2,
    )
    bbox = draw.multiline_textbbox((0, 0), "\n".join(lines), font=font, spacing=4)
    text_width = bbox[2] - bbox[0]
    text_height = bbox[3] - bbox[1]
    x = box[0] + (box[2] - box[0] - text_width) / 2
    y = box[1] + (box[3] - box[1] - text_height) / 2
    draw.multiline_text(
        (x, y),
        "\n".join(lines),
        font=font,
        fill=spec.dark if spec.filename == "game_box_side_were_not_really_strangers.png" else spec.dark,
        spacing=4,
        align="center",
    )


def draw_pattern(
    draw: ImageDraw.ImageDraw,
    area: tuple[int, int, int, int],
    spec: ArtSpec,
) -> None:
    left, top, right, bottom = area
    width = right - left
    height = bottom - top

    if spec.pattern == "bands":
        for offset in range(-height, width, 96):
            draw.polygon(
                (
                    (left + offset, bottom),
                    (left + offset + 32, bottom),
                    (left + offset + height, top),
                    (left + offset + height - 32, top),
                ),
                fill=with_alpha(spec.light, 34),
            )
    elif spec.pattern == "hearts":
        for x in range(left + 70, right, 150):
            for y in range(top + 60, bottom, 120):
                heart(draw, x, y, 22, with_alpha(spec.light, 60))
    elif spec.pattern == "confetti":
        for x in range(left + 50, right, 110):
            draw.rounded_rectangle(
                (x, top + 30, x + 24, bottom - 30),
                radius=12,
                fill=with_alpha(spec.light, 46),
            )
        for y in range(top + 30, bottom, 56):
            draw.polygon(
                ((right - 120, y), (right - 82, y + 18), (right - 120, y + 36), (right - 158, y + 18)),
                fill=with_alpha(spec.accent, 130),
            )
    elif spec.pattern == "rings":
        for radius in (42, 76, 108):
            ring_box = (
                right - 210 - radius,
                top + height // 2 - radius,
                right - 210 + radius,
                top + height // 2 + radius,
            )
            draw.ellipse(ring_box, outline=with_alpha(spec.light, 74), width=8)
        draw.rectangle((left + 40, top + 36, right - 40, top + 56), fill=with_alpha(spec.light, 34))
    elif spec.pattern == "blocks":
        for x in range(left + 30, right, 150):
            draw.rounded_rectangle(
                (x, top + 24, x + 92, top + 82),
                radius=20,
                fill=with_alpha(spec.accent, 112),
            )
            draw.rounded_rectangle(
                (x + 38, bottom - 88, x + 136, bottom - 28),
                radius=20,
                fill=with_alpha(spec.dark, 74),
            )


def heart(
    draw: ImageDraw.ImageDraw,
    x: int,
    y: int,
    size: int,
    fill: tuple[int, int, int, int],
) -> None:
    draw.ellipse((x - size, y - size, x, y), fill=fill)
    draw.ellipse((x, y - size, x + size, y), fill=fill)
    draw.polygon(((x - size, y - size // 3), (x + size, y - size // 3), (x, y + size + 6)), fill=fill)


def fit_text(
    draw: ImageDraw.ImageDraw,
    text: str,
    box: tuple[int, int, int, int],
    font_candidates: Iterable[str],
    min_size: int,
    max_size: int,
    max_lines: int = 3,
) -> tuple[ImageFont.FreeTypeFont, list[str]]:
    max_width = box[2] - box[0]
    max_height = box[3] - box[1]
    for size in range(max_size, min_size - 1, -2):
        font = load_font(font_candidates, size)
        lines = wrap_text(draw, text, font, max_width, max_lines=max_lines)
        if not lines:
            continue
        bbox = draw.multiline_textbbox((0, 0), "\n".join(lines), font=font, spacing=int(font.size * 0.12))
        text_width = bbox[2] - bbox[0]
        text_height = bbox[3] - bbox[1]
        if text_width <= max_width and text_height <= max_height:
            return font, lines
    fallback_font = load_font(font_candidates, min_size)
    return fallback_font, wrap_text(draw, text, fallback_font, max_width, max_lines=max_lines)


def wrap_text(
    draw: ImageDraw.ImageDraw,
    text: str,
    font: ImageFont.FreeTypeFont,
    max_width: int,
    max_lines: int,
) -> list[str]:
    if "\n" in text:
        parts = []
        for paragraph in text.splitlines():
            parts.extend(wrap_text(draw, paragraph, font, max_width, max_lines=max_lines))
        return parts[:max_lines]

    words = text.split()
    if not words:
        return []

    lines: list[str] = []
    current = words[0]

    for word in words[1:]:
        candidate = f"{current} {word}"
        if text_width(draw, candidate, font) <= max_width:
            current = candidate
            continue
        lines.append(current)
        current = word

    lines.append(current)

    if len(lines) <= max_lines:
        return lines

    merged = lines[: max_lines - 1]
    merged.append(" ".join(lines[max_lines - 1 :]))
    while merged and text_width(draw, merged[-1], font) > max_width and " " in merged[-1]:
        last_words = merged[-1].split()
        merged[-1] = " ".join(last_words[:-1])
        overflow = last_words[-1]
        merged.append(overflow)
        if len(merged) > max_lines:
            merged[-2] = f"{merged[-2]} {merged[-1]}".strip()
            merged = merged[:-1]
            break
    return merged[:max_lines]


def text_width(
    draw: ImageDraw.ImageDraw,
    text: str,
    font: ImageFont.FreeTypeFont,
) -> int:
    bbox = draw.textbbox((0, 0), text, font=font)
    return bbox[2] - bbox[0]


def load_font(font_names: Iterable[str], size: int) -> ImageFont.FreeTypeFont:
    for font_name in font_names:
        font_path = FONT_DIR / font_name
        if font_path.exists():
            return ImageFont.truetype(font_path.as_posix(), size=size)
    return ImageFont.load_default(size=size)


def with_alpha(color_hex: str, alpha: int) -> tuple[int, int, int, int]:
    return (*ImageColor.getrgb(color_hex), alpha)


if __name__ == "__main__":
    main()
