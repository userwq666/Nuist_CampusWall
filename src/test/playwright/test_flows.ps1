$testDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$config = "$testDir\cli.config.json"
$testImg = "$testDir\test.png"
$url = "http://localhost:5173"
$ts = [DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds()
$user = "t_$ts"
$pass = "123456"
$email = "$ts@nu.edu.cn"

$env:PLAYWRIGHT_CLI_SESSION = "campuswall-test"

function pw($c) {
    $a = $c -split " "
    & "playwright-cli" "--config" $config @a 2>&1 | Out-Null
    Start-Sleep -Milliseconds 400
}

Write-Output "========== CampusWall 测试 =========="
Write-Output "账号: $user / $pass`n"

Write-Output "[1] 打开首页"
pw "open $url --headed"; Start-Sleep 2

Write-Output "[2] 注册"
pw "goto $url/#/register"; Start-Sleep 1
pw "fill --label=账号 $user"
pw "fill --label=密码 $pass"
pw "fill --label=昵称 测试$ts"
pw "fill --label=教育邮箱 $email"
pw "click --text=注册"; Start-Sleep 2

Write-Output "[3] 登录"
pw "goto $url"; Start-Sleep 1
pw "click --text=登录"; Start-Sleep 1
pw "fill --label=账号 $user"
pw "fill --label=密码 $pass"
pw "click --text=登录"; Start-Sleep 2

Write-Output "[4] 发帖（含图片）"
pw "click --text=发布"; Start-Sleep 1
pw "fill --label=标题 测试帖子 $ts"
pw "fill --label=正文 自动测试发布的帖子。"
pw "upload $testImg"
pw "click --text=发布"; Start-Sleep 3

Write-Output "[5] 帖子详情"
pw "click --text=测试帖子 $ts"; Start-Sleep 2

Write-Output "[6] 评论"
pw "fill e2 测试评论内容"
pw "click --text=发送"; Start-Sleep 2

Write-Output "[7] 回复评论"
pw "click --text=回复"; Start-Sleep 1
pw "fill e2 回复评论内容"
pw "click --text=发送"; Start-Sleep 2

Write-Output "[8] 评论（含图片）"
pw "fill e2 带图的评论"
pw "upload $testImg"
pw "click --text=发送"; Start-Sleep 2

Write-Output "`n全部完成！按 Enter 关闭浏览器"
Read-Host
pw "close"
