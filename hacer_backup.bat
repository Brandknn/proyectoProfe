@echo off
echo ========================================
echo   BACKUP DE BASE DE DATOS MEDICO
echo ========================================
echo.

set FECHA=%date:~-4%%date:~3,2%%date:~0,2%_%time:~0,2%%time:~3,2%
set FECHA=%FECHA: =0%

set BACKUP_FILE=backup_medico_%FECHA%.sql

echo Creando backup de la base de datos...
echo Archivo: %BACKUP_FILE%
echo.

mysqldump -u root -p1702 medico > %BACKUP_FILE%

if %ERRORLEVEL% == 0 (
    echo.
    echo ========================================
    echo   BACKUP CREADO EXITOSAMENTE
    echo   Archivo: %BACKUP_FILE%
    echo ========================================
) else (
    echo.
    echo ========================================
    echo   ERROR AL CREAR EL BACKUP
    echo ========================================
)

pause
