"""
IPv6 Simple Format (v6sf) implementation in Python

Version 0.1

This module provides functions to encode and decode IPv6 addresses
according to the IPv6 Simple Format specification.

Features:
- Base35 encoding (lowercase, without 'o')
- Checksum calculation and validation
- Optional block separation (4 characters per block)
- Prefix 'v6sf:' handling
- Fully reversible mapping

Licensed under Apache License 2.0
"""

from typing import Optional

PREFIX = "v6sf:"
CHARSET = "0123456789abcdefghijklmnpqrstuvwxyz"  # Base35 character set
BASE = len(CHARSET)
BLOCK_SIZE = 4  # Optional block separation


def encode(ipv6_expanded: str, add_blocks: bool = True) -> str:
    """
    Encode a fully expanded IPv6 address into v6sf format.

    :param ipv6_expanded: Fully expanded IPv6 (8 blocks of 4 hex digits)
    :param add_blocks: Whether to insert block separators every 4 chars
    :return: Encoded v6sf string with checksum and prefix
    :raises ValueError: if IPv6 is invalid
    """
    if ipv6_expanded is None:
        raise ValueError("IPv6 address cannot be None")

    # Remove colons and normalize
    hex_str = ipv6_expanded.replace(":", "").lower()
    if len(hex_str) != 32:
        raise ValueError("IPv6 must be fully expanded (32 hex digits)")

    # Convert to integer
    value = int(hex_str, 16)

    # Encode to base35
    encoded = ""
    current = value
    while current > 0:
        remainder = current % BASE
        encoded = CHARSET[remainder] + encoded
        current //= BASE

    # Pad to fixed length (ceil(log_35(2^128)) ≈ 25)
    while len(encoded) < 25:
        encoded = "0" + encoded

    # Append checksum
    checksum_char = _calculate_checksum(encoded)
    encoded += checksum_char

    # Add block separators
    if add_blocks:
        encoded = _add_block_separators(encoded)

    return PREFIX + encoded


def decode(v6sf: str) -> str:
    """
    Decode a v6sf string into a fully expanded IPv6 address.

    :param v6sf: v6sf encoded string
    :return: Fully expanded IPv6 address
    :raises ValueError: if input is invalid or checksum fails
    """
    if not v6sf.lower().startswith(PREFIX):
        raise ValueError(f"Input must start with prefix {PREFIX}")

    # Remove prefix and colons
    clean = v6sf[len(PREFIX):].replace(":", "").lower()
    if len(clean) < 2:
        raise ValueError("Encoded value too short")

    # Separate checksum
    checksum_char = clean[-1]
    encoded = clean[:-1]

    # Validate checksum
    if checksum_char != _calculate_checksum(encoded):
        raise ValueError("Invalid checksum")

    # Decode base35 to integer
    value = 0
    for c in encoded:
        index = CHARSET.find(c)
        if index == -1:
            raise ValueError(f"Invalid character in encoded string: {c}")
        value = value * BASE + index

    # Convert to 32-char hex
    hex_str = hex(value)[2:].rjust(32, "0")

    # Format into IPv6 blocks
    ipv6_blocks = [hex_str[i:i+4] for i in range(0, 32, 4)]
    return ":".join(ipv6_blocks)


def _calculate_checksum(encoded: str) -> str:
    """
    Calculate a simple checksum character for a base35 string.

    :param encoded: Base35 string without checksum
    :return: Single checksum character
    """
    total = sum(CHARSET.index(c) for c in encoded)
    return CHARSET[total % BASE]


def _add_block_separators(s: str) -> str:
    """
    Add colon separators every BLOCK_SIZE characters.

    :param s: Input string
    :return: Block separated string
    """
    return ":".join(s[i:i+BLOCK_SIZE] for i in range(0, len(s), BLOCK_SIZE))
