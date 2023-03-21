# Encode Text Into Image
This program encodes text into image. For now, only supported image format is .bmp, without compression, bits per pixel = 24 (3 bytes for every pixel, RGB bytes).

## Implementation
Whole encoding/decoding logic is given in Class [Converter](https://github.com/nikola00nikola/Encode-Text-Into-Image/blob/main/converter/Converter.java). One byte of pixel (R, G or B) contains only 1 bit of input character, and it is the Least Significant Bit, so value of byte is changed for +/- 1 at worst case, so the pixel difference can't be seen.

Beacuse this is made for fun text is not encrypted before saving into image, and saving goes sequential from the first pixel byte (the simplest way).
