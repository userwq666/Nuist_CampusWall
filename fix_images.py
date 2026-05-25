import re

with open('frontend/src/views/post/PostDetailView.vue', 'r', encoding='utf-8') as f:
    content = f.read()

# 1) Replace post image with el-image + preview
content = content.replace(
    '<div v-if="post.imageUrl" class="detail-image">\n        <img :src="post.imageUrl" :alt="post.title" @error="onImageError" />\n      </div>',
    '<div v-if="post.imageUrl" class="detail-image">\n        <el-image\n          :src="post.imageUrl"\n          :alt="post.title"\n          :preview-src-list="[post.imageUrl]"\n          fit="contain"\n          :preview-teleported="true"\n          class="detail-el-image"\n        />\n      </div>'
)
print('1) Post image updated')

# 2) Replace comment image with el-image + preview
content = content.replace(
    '<div v-if="comment.imageUrl" class="comment-image">\n              <img :src="comment.imageUrl" @error="onImageError" />\n            </div>',
    '<div v-if="comment.imageUrl" class="comment-image">\n              <el-image\n                :src="comment.imageUrl"\n                :preview-src-list="[comment.imageUrl]"\n                fit="contain"\n                :preview-teleported="true"\n                class="comment-el-image"\n              />\n            </div>'
)
print('2) Comment image updated')

# 3) Update CSS for detail-image
content = content.replace(
    '.detail-image {\n  margin-bottom: 20px;\n  border-radius: var(--radius-md);\n  overflow: hidden;\n}\n.detail-image img { width: 100%; display: block; }',
    '.detail-image {\n  margin-bottom: 20px;\n  border-radius: var(--radius-md);\n  overflow: hidden;\n  max-width: 600px;\n}\n.detail-image .detail-el-image {\n  width: 100%;\n  max-height: 500px;\n  display: block;\n  cursor: zoom-in;\n}'
)
print('3) CSS updated for detail-image')

# 4) Update comment image CSS
content = content.replace(
    '.comment-image { margin-bottom: 6px; border-radius: 8px; overflow: hidden; max-width: 240px; }\n.comment-image img { width: 100%; display: block; }',
    '.comment-image { margin-bottom: 6px; border-radius: 8px; overflow: hidden; max-width: 240px; }\n.comment-image .comment-el-image { width: 100%; display: block; cursor: zoom-in; }'
)
print('4) CSS updated for comment-image')

with open('frontend/src/views/post/PostDetailView.vue', 'w', encoding='utf-8') as f:
    f.write(content)
print('All done')