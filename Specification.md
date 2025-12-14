# IPv6 Simple Format Specification

Part of the IPv6 Simple Format Project

Version 0.1

Copyright 2025 Nico Strecker

Licensed under the Apache License, Version 2.0


## 1. Introduction

This document defines the IPv6 Simple Format (v6sf), a human friendly, lossless and fully reversible textual representation of IPv6 addresses.

The IPv6 Simple Format is designed to address the challenges of manually communicating IPv6 addresses between humans. While IPv6 provides a standardized shorthand notation, this notation remains difficult to read aloud, transcribe correctly and verify visually. v6sf introduces an alternative representation optimized for application layer usage where humans interact directly with IPv6 addresses.

This specification defines the encoding rules, decoding rules, character set, checksum mechanism and validation requirements. It does not define network protocols and does not alter IPv6 semantics.


## 2. Terminology

The key words “must”, “must not”, “should” and “may” in this document are to be interpreted as described in RFC 2119.

IPv6 address refers to a valid 128 bit IPv6 address as defined by existing IPv6 standards.

Encoded value refers to the textual representation produced by the IPv6 Simple Format encoding process.


## 3. Design Goals

The IPv6 Simple Format is designed according to the following goals.

Human friendly representation suitable for reading, dictation and transcription  
Lossless and deterministic encoding  
Strict one to one mapping between IPv6 addresses and encoded values  
Avoidance of ambiguous characters  
Case insensitive decoding  
Simple and portable implementation  
Clear identification of encoded values  


## 4. Operating Layer and Usage Model

The IPv6 Simple Format operates exclusively at the application layer.

Applications may accept v6sf values as user input, display them in user interfaces or store them in configuration files. Before any networking operation is performed, the encoded value must be decoded to a canonical IPv6 address.

Applications should display the fully expanded IPv6 address to the user after decoding to allow explicit verification. This step is strongly recommended to reduce misconfiguration and transcription errors.


## 5. Input Normalization

Before encoding, an IPv6 address must be converted to its fully expanded form.

All shorthand notation using double colons must be resolved. Each IPv6 address must consist of exactly eight hexadecimal blocks, each block containing four hexadecimal digits. Leading zeros must be preserved.

Example.

Input  
2001:db8::1  

Normalized form  
2001:0db8:0000:0000:0000:0000:0000:0001  

This normalization step ensures a single canonical representation for each IPv6 address and prevents multiple encodings of the same address.


## 6. Binary Representation

After normalization, the IPv6 address is interpreted as a 128 bit unsigned integer in network byte order.

This integer value represents the complete IPv6 address and is the sole input to the encoding process. No additional metadata, flags or context information is included.


## 7. Character Set

The IPv6 Simple Format uses a base 35 character set defined as follows.

0123456789abcdefghijklmnpqrstuvwxyz

The letter o is intentionally excluded to prevent confusion with the digit 0 in written and spoken communication.

All encoded output must use lowercase characters. Decoders must treat input as case insensitive.


## 8. Rationale for Base 35 Encoding

The choice of base 35 is a fundamental design decision.

Higher base encodings such as base 36 or base 58 increase information density but introduce characters that are visually or verbally ambiguous, including mixed case letters and symbols that are easily confused in many fonts and environments.

Lower base encodings such as base 32 avoid ambiguity but significantly increase the length of the encoded output, making manual communication less practical and increasing the likelihood of transcription errors.

Base 35 provides an optimal balance by maximizing density while excluding all commonly confused characters. It allows a compact representation using only lowercase alphanumeric characters and enables reliable communication across a wide range of human interfaces, including voice, handwriting and plain text environments.


## 9. Encoding Procedure

The encoding process consists of the following steps.

1. Normalize the IPv6 address to its fully expanded form.  
2. Interpret the normalized address as a 128 bit unsigned integer.  
3. Convert the integer value to a base 35 representation using the defined character set.  
4. Append a single checksum character to the encoded sequence.  
5. Prepend the v6sf: prefix to identify the format.  

The resulting value is the IPv6 Simple Format representation.


## 10. Checksum Definition

The checksum is intended to detect common transcription and input errors.

The checksum character is calculated by summing the numeric values of all encoded characters and applying a modulo 35 operation. The resulting value is mapped to a character using the same base 35 character set.

Decoders must validate the checksum and should reject encoded values with an invalid checksum. The checksum is not intended to provide cryptographic integrity or security guarantees.


## 11. Prefix and Identification

Every encoded value must begin with the fixed prefix.

v6sf:

The prefix clearly identifies the format and prevents accidental interpretation as a raw IPv6 address or another encoding.

The prefix is not part of the encoded data and must be ignored during decoding.


## 12. Block Separation

For improved readability, the encoded character sequence may be split into blocks of four characters separated by colons.

Block separators are optional and have no semantic meaning. Decoders must ignore all block separators.

Example.

v6sf:abcd:efgh:ijkl:mnop:qrst:u  

The same value without block separation is equally valid.


## 13. Decoding Procedure

Decoding reverses the encoding process.

1. Verify and remove the v6sf: prefix.  
2. Remove all block separators.  
3. Validate the checksum character.  
4. Convert the base 35 value to a 128 bit unsigned integer.  
5. Convert the integer to an IPv6 address in fully expanded form.  

The decoded IPv6 address must exactly match the original normalized address used during encoding.


## 14. Examples

Example IPv6 address.

2001:0db8:0000:0000:0000:0000:0000:0001  

Encoded representation.

v6sf:1n9g:k4r2:f82d:z  

Example with block separation removed.

v6sf:1n9gk4r2f82dz  

Both representations decode to the same IPv6 address.


## 15. Address Semantics

The IPv6 Simple Format does not modify IPv6 semantics.

Global unicast, link local, unique local, multicast and other special IPv6 address ranges are encoded and decoded identically. Interpretation of address type remains the responsibility of the application.


## 16. Error Handling

Implementations should reject encoded values that contain invalid characters, invalid checksum values, missing prefixes or malformed input lengths.

Implementations must not attempt automatic error correction beyond checksum validation.


## 17. Security Considerations

The IPv6 Simple Format does not introduce new security risks beyond those inherent in handling IPv6 addresses.

The format does not obscure or encrypt addresses and must not be used as a security mechanism.


## 18. Conformance

An implementation conforms to this specification if it correctly implements all encoding and decoding rules defined in this document and preserves a strict one to one mapping between IPv6 addresses and encoded values.


## 19. License

This document is licensed under the Apache License, Version 2.0.
