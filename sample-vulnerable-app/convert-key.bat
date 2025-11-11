@echo off
echo GitHub App Private Key Converter
echo ===================================
echo.
echo Checking for OpenSSL...
where openssl >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo OpenSSL found! Converting key...
    openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in "src/main/resources/was-authentication-test.2025-11-06.private-key.pem" -out "src/main/resources/was-authentication-test.2025-11-06.private-key-pkcs8.pem"
    if %ERRORLEVEL% EQU 0 (
        echo Success! Converted key saved as: src/main/resources/was-authentication-test.2025-11-06.private-key-pkcs8.pem
    ) else (
        echo Error converting key!
    )
) else (
    echo OpenSSL not found!
    echo Please install OpenSSL or use online converter:
    echo https://8gwifi.org/PemParserFunctions.jsp
)
echo.
pause
