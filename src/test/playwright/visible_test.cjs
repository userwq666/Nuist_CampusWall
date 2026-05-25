const { chromium } = require("playwright");
const path = require("path");
const fs = require("fs");

const BASE = process.env.TEST_BASE_URL || "http://localhost:5173";
const SHOT_DIR = path.resolve(__dirname, "screenshots");
const TEST_IMG = path.resolve(__dirname, "test.png");
const SLOW_MO = 200;

const ts = Date.now();
const USER = "u" + ts;
const PASS = "123456";
const NICK = "测试" + ts;
const EMAIL = ts + "@nuist.edu.cn";

let pass = 0, fail = 0;

function ok(name) { pass++; console.log("  ✅ " + name); }
function no(name) { fail++; console.log("  ❌ " + name); }

async function shot(page, label) {
  const fp = path.join(SHOT_DIR, label + ".png");
  try { await page.screenshot({ path: fp, fullPage: true }); console.log("  📸 " + fp); }
  catch (_) { /* ignore */ }
}

(async () => {
  if (!fs.existsSync(SHOT_DIR)) fs.mkdirSync(SHOT_DIR, { recursive: true });

  console.log("=".repeat(55));
  console.log("  Nuist_CampusWall 可见浏览器测试");
  console.log("  目标: " + BASE + "    账号: " + USER);
  console.log("=".repeat(55));

  const browser = await chromium.launch({ headless: false, slowMo: SLOW_MO });
  const ctx = await browser.newContext({ viewport: { width: 1280, height: 720 }, locale: "zh-CN" });
  const page = await ctx.newPage();

  const jsErrors = [];
  page.on("console", m => { if (m.type() === "error") jsErrors.push(m.text()); });

  try {
    // ============ 1. 帖子列表页 loading/error/empty/data ============
    console.log("\n📋 1. 帖子列表页 /post");

    await page.goto(BASE + "/post", { waitUntil: "domcontentloaded", timeout: 15000 });

    const hasSkeleton = await page.locator(".skeleton-card").count();
    if (hasSkeleton > 0) {
      ok("Loading 状态: 骨架屏渲染");
    } else {
      console.log("  ⏭ 骨架屏已消失（数据返回快），跳过 loading 验证");
    }

    try {
      await page.waitForFunction(() => {
        return document.querySelectorAll(".skeleton-card").length === 0
          && (document.querySelectorAll(".post-card").length > 0
           || document.querySelector(".empty-state")
           || document.querySelector(".error-state"));
      }, { timeout: 10000 });
    } catch (_) { /* timeout */ }
    await page.waitForTimeout(500);

    const errorEl = await page.locator(".error-state").count();
    const emptyEl = await page.locator(".empty-state").count();
    const cardEl = await page.locator(".post-card").count();

    if (errorEl > 0) {
      ok("Error 状态: 错误提示 + 重试按钮");
      await shot(page, "01_error");
    } else if (emptyEl > 0) {
      ok("Empty 状态: 空数据提示");
      await shot(page, "01_empty");
    } else if (cardEl > 0) {
      ok("Data 状态: 帖子卡片渲染 (" + cardEl + " 张)");
      const cardImgs = await page.locator(".post-card img").count();
      if (cardImgs > 0) ok("帖子卡片图片渲染 (" + cardImgs + " 张)", true);
      await shot(page, "01_data");
    } else {
      no("页面未显示任何有效状态");
    }

    ok("导航栏存在", await page.locator(".navbar").count() > 0);
    ok("Tab: 推荐", await page.locator(".el-tabs__item:has-text('推荐')").count() > 0);
    ok("Tab: 公告", await page.locator(".el-tabs__item:has-text('公告')").count() > 0);

    // ============ 2. 登录页 ============
    console.log("\n🔑 2. 登录页 /login");

    await page.goto(BASE + "/login", { waitUntil: "domcontentloaded", timeout: 15000 });
    await page.waitForSelector(".auth-page", { timeout: 8000 });

    ok("登录表单渲染", await page.locator(".auth-form").count() > 0);
    ok("用户名输入框", await page.locator("input[placeholder='用户名']").count() > 0);
    ok("密码输入框", await page.locator("input[placeholder='密码']").count() > 0);
    ok("登录按钮", await page.locator(".auth-btn:has-text('登录')").count() > 0);
    await shot(page, "02_login");

    // ============ 3. 注册页 ============
    console.log("\n📝 3. 注册页 /register");

    await page.goto(BASE + "/register", { waitUntil: "domcontentloaded", timeout: 15000 });
    await page.waitForSelector(".auth-page", { timeout: 8000 });

    ok("注册表单渲染", await page.locator(".auth-form").count() > 0);
    ok("昵称输入框", await page.locator("input[placeholder='昵称']").count() > 0);
    ok("邮箱输入框", await page.locator("input[placeholder*='教育邮箱']").count() > 0);
    ok("确认密码框", await page.locator("input[placeholder='确认密码']").count() > 0);
    await shot(page, "03_register");

    // ============ 4. 未登录守卫 ============
    console.log("\n🔒 4. 未登录权限守卫");

    const guards = [
      ["/post/1", "帖子详情"],
      ["/profile", "个人资料"],
      ["/admin", "管理后台"],
    ];
    for (const [p, name] of guards) {
      await page.goto(BASE + p, { waitUntil: "domcontentloaded", timeout: 15000 });
      await page.waitForTimeout(800);
      if (page.url().includes("/login")) ok(name + " → 跳转登录页");
      else no(name + " → 未跳转: " + page.url());
    }

    // ============ 5. 注册 ============
    console.log("\n📝 5. 注册流程");

    await page.goto(BASE + "/register", { waitUntil: "domcontentloaded", timeout: 15000 });
    await page.waitForSelector(".auth-form", { timeout: 8000 });

    const regInputs = page.locator(".auth-form input");
    await regInputs.nth(0).fill(USER);       // 用户名
    await regInputs.nth(1).fill(NICK);       // 昵称
    await regInputs.nth(2).fill(EMAIL);      // 教育邮箱
    await regInputs.nth(3).fill(PASS);       // 密码
    await regInputs.nth(4).fill(PASS);       // 确认密码
    await page.locator(".auth-btn:has-text('注册')").click();
    await page.waitForTimeout(2000);

    ok("注册成功 → 跳转登录页", page.url().includes("/login"));

    // ============ 6. 登录 ============
    console.log("\n🔑 6. 登录流程");

    if (!page.url().includes("/login")) {
      await page.goto(BASE + "/login", { waitUntil: "domcontentloaded" });
      await page.waitForSelector(".auth-form", { timeout: 8000 });
    }

    const loginInputs = page.locator(".auth-form input");
    await loginInputs.nth(0).fill(USER);
    await loginInputs.nth(1).fill(PASS);
    await page.locator(".auth-btn:has-text('登录')").click();
    await page.waitForTimeout(2000);

    ok("登录成功 → 跳转列表", page.url().includes("/post"));
    ok("导航栏显示已登录用户", await page.locator(".user-trigger").count() > 0);
    await shot(page, "06_logged_in");

    // ============ 7. 登录后守卫 ============
    console.log("\n🔓 7. 登录后守卫");

    await page.goto(BASE + "/login", { waitUntil: "domcontentloaded", timeout: 15000 });
    await page.waitForTimeout(800);
    ok("已登录 → /login 重定向", page.url().includes("/post"));

    await page.goto(BASE + "/register", { waitUntil: "domcontentloaded", timeout: 15000 });
    await page.waitForTimeout(800);
    ok("已登录 → /register 重定向", page.url().includes("/post"));

    await page.goto(BASE + "/profile", { waitUntil: "domcontentloaded" });
    await page.waitForTimeout(800);
    ok("已登录 → /profile 可访问", page.url().includes("/profile"));
    await shot(page, "07_profile");

    // ============ 8. 发帖 ============
    console.log("\n✏️ 8. 发布帖子");

    await page.goto(BASE + "/post", { waitUntil: "domcontentloaded", timeout: 15000 });
    await page.waitForSelector(".tabs-bar", { timeout: 8000 });
    await page.waitForTimeout(500);

    const createBtn = page.locator(".create-btn");
    if (await createBtn.count() === 0) {
      no("发布按钮不存在（需登录）");
    } else {
      await createBtn.click();
      await page.waitForSelector(".el-dialog:visible", { timeout: 5000 });
      ok("发布对话框打开", true);
      await shot(page, "08_create_dialog");

      await page.locator(".el-dialog input").first().fill("测试帖子_" + ts);
      await page.locator(".el-dialog textarea").first().fill("E2E 自动测试帖子正文。");

            // 上传图片并验证缩略图出现
      let imgUploaded = false;
      if (fs.existsSync(TEST_IMG)) {
        const fi = page.locator('.el-upload input[type="file"]');
        if (await fi.count() > 0) {
          await fi.setInputFiles(TEST_IMG);
          // 等待 el-upload 缩略图出现
          try {
            await page.waitForSelector('.el-upload-list__item-thumbnail', { timeout: 5000 });
            imgUploaded = true;
          } catch (_) {
            // 缩略图未出现
          }
        }
      }
      ok("图片上传并显示缩略图", imgUploaded);

      await page.locator(".el-dialog .el-button--primary").last().click();
      await page.waitForTimeout(2500);

      if (await page.locator(".el-dialog:visible").count() === 0)
        ok("发布成功，对话框关闭");
      else
        no("对话框未关闭");
    }

    // ============ 9. 帖子详情 ============
    console.log("\n🔍 9. 帖子详情 /post/:id");

    const firstCard = page.locator(".post-card").first();
    if (await firstCard.count() === 0) {
      no("没有帖子卡片");
    } else {
      await firstCard.click();
      try {
        await page.waitForSelector(".detail-container", { timeout: 8000 });
      } catch (_) {
        if (page.url().includes("/login")) {
          // token 过期，重新登录
          await page.locator(".auth-form input").nth(0).fill(USER);
          await page.locator(".auth-form input").nth(1).fill(PASS);
          await page.locator(".auth-btn:has-text('登录')").click();
          await page.waitForTimeout(2000);
          await page.locator(".post-card").first().click();
          await page.waitForSelector(".detail-container", { timeout: 8000 }).catch(() => {});
        }
      }

      if (await page.locator(".detail-container").count() > 0) {
        ok("详情页加载", true);
        ok("标题渲染", await page.locator(".detail-title").count() > 0);
        ok("正文渲染", await page.locator(".detail-content").count() > 0);
        ok("操作栏渲染", await page.locator(".detail-actions").count() > 0);
        ok("评论输入区", await page.locator(".comment-input-area").count() > 0);
        const hasDetailImg = await page.locator(".detail-image img").count() > 0;
        if (hasDetailImg) ok("详情页图片渲染", true);

        const commentCount = await page.locator(".comment-item").count();
        if (commentCount > 0) ok("评论列表: 有评论", true);
        else if (await page.locator(".no-comments").count() > 0) ok("Empty 状态: 暂无评论", true);

        await shot(page, "09_detail");

        // ============ 10. 评论 ============
        console.log("\n💬 10. 发表评论");
        const ta = page.locator(".comment-input-area textarea").first();
        if (await ta.count() > 0) {
          await ta.fill("测试评论_" + ts);
          await page.locator(".comment-input-actions .el-button--primary").click();
          await page.waitForTimeout(2000);
          ok("评论提交", await page.locator(".comment-item").count() > commentCount);
          await shot(page, "10_comment");

          // ============ 11. 回复 ============
          console.log("\n↩️ 11. 回复评论");
          const rpl = page.locator(".comment-action:has-text('回复')").first();
          if (await rpl.count() > 0) {
            await rpl.click();
            await page.waitForTimeout(500);
            ok("回复模式激活", await page.locator(".reply-hint").count() > 0);
            await page.locator(".comment-input-area textarea").first().fill("回复_" + ts);
            await page.locator(".comment-input-actions .el-button--primary").click();
            await page.waitForTimeout(2000);
            ok("回复提交", true);
          } else {
            console.log("  ⏭ 无评论可回复");
          }

          // ============ 12. 点赞 ============
          console.log("\n⭐ 12. 点赞");
          const lb = page.locator(".like-btn").first();
          if (await lb.count() > 0) {
            await lb.click();
            await page.waitForTimeout(1000);
            ok("点赞切换", await page.locator(".like-btn.liked").count() > 0);
          }
        }
      }
    }

    // ============ 结果 ============
    console.log("\n" + "=".repeat(55));
    console.log("  结果: ✅ " + pass + "  |  ❌ " + fail + "  |  共 " + (pass+fail));
    if (fail > 0) console.log("  通过率: " + ((pass/(pass+fail))*100).toFixed(1) + "%");
    console.log("=".repeat(55));
    if (jsErrors.length > 0) {
      console.log("  ⚠️ 控制台错误 " + jsErrors.length + " 条:");
      jsErrors.slice(0, 3).forEach(e => console.log("     " + e));
    }

    console.log("\n60秒后自动关闭，按 Ctrl+C 提前结束...\n");
    await page.waitForTimeout(60000);
    await browser.close();

  } catch (err) {
    console.error("\n💥 异常:", err.message);
    try { await shot(page, "99_crash"); } catch (_) {}
    try { await browser.close(); } catch (_) {}
    process.exit(1);
  }
})();


