param (
	[switch]$getCurrentVersion = $false,
	[switch]$createPackage = $false,
	[switch]$createZip= $false,
	[switch]$createTag = $false,
    [switch]$installDependancy = $false,
    [switch]$uploadNexus = $false,
    [switch]$installer = $false
)
$filePath = "version.xml"
$msbuild ='"C:\Program Files (x86)\MSBuild\12.0\Bin\MSBuild.exe"'

function GetCurrentVersion()
{
    Write-Host  $msbuild
    Write-Host "$env:BUILD_NUMBER"
    Write-Host "$env:package"
    # Read in the file contents and return the version node's value.
    [ xml ]$fileContents = Get-Content -Path $filePath
    $version = $fileContents.project.AssemblyVersion
    return $version
}

function ExecuteInstallar()
{
    $args = '"-version"'
    iex ("& {0} {1}" -f $msbuild, $args)

}
# Calling the procedure if any was requested by the caller
if ($getCurrentVersion) { GetCurrentVersion }
if ($createPackage) { CreatePackage }
if ($createZip) { CreateZip }
if ($createTag) { CreateTag }
if ($installDependancy) { InstallDependancy }
if ($uploadNexus) { UploadNexus }
if ($installer) {ExecuteInstallar}
