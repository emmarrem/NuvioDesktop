#requires -Version 7.0
[CmdletBinding()]
param([Parameter(Mandatory)][string]$SourceDirectory)
$ErrorActionPreference = 'Stop'
$supportRoot = (Resolve-Path "$PSScriptRoot/../..").Path
$source = (Resolve-Path -LiteralPath $SourceDirectory).Path
$metadata = Get-Content "$supportRoot/ports/windows-arm64/base.json" -Raw | ConvertFrom-Json
& git -C $source status --porcelain | ForEach-Object { throw 'Selected upstream source must be clean before applying the port.' }
& git -C $source fetch --depth 1 https://github.com/NuvioMedia/NuvioDesktop.git $metadata.baseCommit
if ($LASTEXITCODE) { throw 'Unable to fetch the port base for three-way application.' }
& git -C $source -c core.autocrlf=false apply --3way --index "$supportRoot/ports/windows-arm64/app.patch"
if ($LASTEXITCODE) { throw 'The selected release conflicts with this ARM64 port. Update ports/windows-arm64/app.patch for the new upstream source; no binary was built.' }
Write-Output 'ARM64 port applied to the selected official source.'
