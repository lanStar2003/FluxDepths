# Copies the FluxDepths quest lines into a GTNH instance's DefaultQuests and adds them to the quest book order.
# Usage (PowerShell):
#   .\quests\install.ps1 -Instance "E:\Game\HMCL\.minecraft\versions\GT New Horizons 2.8.4"
#   .\quests\install.ps1 -Instance "<instance>" -Line lazyae      # only the lazy AE line
#   .\quests\install.ps1 -Instance "<instance>" -Line shards      # only the shard collector line (needs the mod)
# Then in game: /bq_admin default load   (quest progress is kept; a GTNH update replaces DefaultQuests, run this again)
param(
    [Parameter(Mandatory = $true)][string]$Instance,
    [ValidateSet('all', 'lazyae', 'shards')][string]$Line = 'all'
)
$ErrorActionPreference = 'Stop'
$src = Join-Path $PSScriptRoot 'DefaultQuests'
$dst = Join-Path $Instance 'config\betterquesting\DefaultQuests'
if (-not (Test-Path $dst)) { throw "No quest book here: $dst" }

$backup = "$dst.backup-$(Get-Date -Format yyyyMMdd-HHmmss)"
Copy-Item $dst $backup -Recurse
Write-Host "Backup: $backup"

$prefixes = @{ lazyae = 'LazyAE-'; shards = 'FluxDepthsShards-' }
$chosen = if ($Line -eq 'all') { @('lazyae', 'shards') } else { @($Line) }
$ids = @()
foreach ($l in $chosen) {
    foreach ($kind in 'Quests', 'QuestLines') {
        Get-ChildItem (Join-Path $src $kind) -Directory -Filter "$($prefixes[$l])*" | ForEach-Object {
            $target = Join-Path (Join-Path $dst $kind) $_.Name
            if (Test-Path $target) { Remove-Item $target -Recurse -Force }
            Copy-Item $_.FullName $target -Recurse
            if ($kind -eq 'QuestLines') { $ids += $_.Name.Substring($prefixes[$l].Length) }
        }
    }
}

$order = Join-Path $dst 'QuestLinesOrder.txt'
$utf8 = New-Object System.Text.UTF8Encoding($false)
$existing = [System.IO.File]::ReadAllLines($order, $utf8)
$text = [System.IO.File]::ReadAllText($order, $utf8)
if ($text.Length -gt 0 -and -not $text.EndsWith("`n")) { [System.IO.File]::AppendAllText($order, "`r`n", $utf8) }
$added = 0
foreach ($entry in [System.IO.File]::ReadAllLines((Join-Path $src 'QuestLinesOrder.add.txt'), $utf8)) {
    $id = $entry.Split(':')[0]
    if ($ids -notcontains $id) { continue }
    if ($existing | Where-Object { $_.StartsWith("${id}:") }) { continue }
    [System.IO.File]::AppendAllText($order, "$entry`r`n", $utf8)
    $added++
}
Write-Host "Copied $($chosen -join ', '); $added new line(s) in the quest book order."
Write-Host 'In game, run: /bq_admin default load'
