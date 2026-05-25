const fs = require('fs');
let content = fs.readFileSync('frontend/src/views/post/PostDetailView.vue', 'utf8');

// Replace old CSS
const oldCss = `/* Comment Input */
.comment-input-area { margin-bottom: 24px; }
.comment-input { --el-input-border-radius: 12px; }
.comment-input-area-upload { margin-bottom: 8px; }
.comment-input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}
.reply-hint {`;

const newCss = `/* Comment Bottom Bar */
.comment-bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  border-top: 1px solid var(--border);
  padding: 8px 16px;
  z-index: 100;
  box-shadow: 0 -2px 8px rgba(0,0,0,0.08);
}
.comment-bar-inner {
  display: flex;
  align-items: center;
  gap: 8px;
}
.comment-upload-btn { flex-shrink: 0; }
.comment-input { flex: 1; --el-input-border-radius: 20px; }
.comment-bar-file-hint { margin-top: 4px; }
.comment-bar-reply {
  font-size: 12px;
  color: var(--primary);
  margin-top: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}
.comment-bar-reply .el-icon { cursor: pointer; }
.reply-hint {`;

if (content.includes(oldCss)) {
  content = content.replace(oldCss, newCss);
  console.log('CSS replaced successfully');
} else {
  console.log('Old CSS pattern not found!');
  // Find the comment input section
  const idx = content.indexOf('/* Comment Input */');
  if (idx >= 0) {
    console.log('Found at index', idx);
    console.log('Context:', content.substring(idx, idx + 300));
  }
}

// Add bottom padding for the fixed bar
if (!content.includes('padding-bottom: 80px')) {
  content = content.replace('</style>', '.detail-page { padding-bottom: 80px; }\n</style>');
  console.log('Added bottom padding');
}

fs.writeFileSync('frontend/src/views/post/PostDetailView.vue', content, 'utf8');
console.log('Done');
