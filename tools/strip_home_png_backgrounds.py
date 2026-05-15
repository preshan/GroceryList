#!/usr/bin/env python3
"""Remove near-uniform edge background from home PNGs (transparent alpha)."""
from __future__ import annotations

import math
import sys
from pathlib import Path

from PIL import Image


def edge_mean_rgb(rgb: Image.Image) -> tuple[float, float, float]:
    w, h = rgb.size
    rs = gs = bs = 0.0
    n = 0
    for x in range(w):
        for y in (0, h - 1):
            p = rgb.getpixel((x, y))
            rs += p[0]
            gs += p[1]
            bs += p[2]
            n += 1
    for y in range(h):
        for x in (0, w - 1):
            p = rgb.getpixel((x, y))
            rs += p[0]
            gs += p[1]
            bs += p[2]
            n += 1
    return rs / n, gs / n, bs / n


def strip_background(path: Path, threshold: float, soft: float) -> None:
    img = Image.open(path).convert("RGBA")
    w, h = img.size
    bg = edge_mean_rgb(img.convert("RGB"))
    br, bgc, bb = bg
    out = Image.new("RGBA", (w, h))
    src = img.load()
    dst = out.load()
    for y in range(h):
        for x in range(w):
            r, g, b, a = src[x, y]
            d = math.sqrt((r - br) ** 2 + (g - bgc) ** 2 + (b - bb) ** 2)
            if d <= threshold:
                dst[x, y] = (r, g, b, 0)
            elif d <= threshold + soft:
                t = (d - threshold) / soft
                na = int(max(0, min(255, round(t * 255))))
                dst[x, y] = (r, g, b, na)
            else:
                dst[x, y] = (r, g, b, a)
    out.save(path, format="PNG", optimize=True)
    print(f"Wrote {path} (bg≈{tuple(round(c) for c in bg)})")


def main() -> None:
    root = Path(__file__).resolve().parents[1] / "app" / "src" / "main" / "res" / "drawable-nodpi"
    # threshold / soft tuned for light solid or near-solid edge backgrounds
    jobs = [
        (root / "home_welcome_art.png", 36, 18),
        (root / "home_hero_bag.png", 38, 20),
        (root / "home_ad_basket.png", 42, 22),
    ]
    for path, th, sf in jobs:
        if not path.is_file():
            print(f"Skip missing {path}", file=sys.stderr)
            continue
        strip_background(path, th, sf)


if __name__ == "__main__":
    main()
