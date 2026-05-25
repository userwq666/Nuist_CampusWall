c = open('frontend/src/views/post/PostDetailView.vue', encoding='utf-8').read()

# 1) Fix template: move nav buttons outside image-wrapper, into detail-image directly
old_temp = '''      <div v-if="post.imageUrl" class="detail-image">
        <div class="image-wrapper">
          <el-image
            :src="images[currentImageIndex]"
            :alt="post.title"
            :preview-src-list="images"
            fit="contain"
            :preview-teleported="true"
            :initial-index="currentImageIndex"
            class="detail-el-image"
          />
          <button v-if="images.length > 1" class="img-nav-btn img-nav-left" @click.stop="prevImage">
            <el-icon><ArrowLeft /></el-icon>
          </button>
          <button v-if="images.length > 1" class="img-nav-btn img-nav-right" @click.stop="nextImage">
            <el-icon><ArrowRight /></el-icon>
          </button>
          <div v-if="images.length > 1" class="img-dots">
            <span v-for="(img, idx) in images" :key="idx" class="dot" :class="{ active: idx === currentImageIndex }"></span>
          </div>
        </div>
      </div>'''

new_temp = '''      <div v-if="post.imageUrl" class="detail-image">
        <div class="img-stage">
          <el-image
            :src="images[currentImageIndex]"
            :alt="post.title"
            :preview-src-list="images"
            fit="contain"
            :preview-teleported="true"
            :initial-index="currentImageIndex"
            class="detail-el-image"
          />
          <button v-if="images.length > 1" class="img-nav-btn img-nav-left" @click.stop="prevImage">
            <el-icon><ArrowLeft /></el-icon>
          </button>
          <button v-if="images.length > 1" class="img-nav-btn img-nav-right" @click.stop="nextImage">
            <el-icon><ArrowRight /></el-icon>
          </button>
        </div>
        <div v-if="images.length > 1" class="img-dots">
          <span v-for="(img, idx) in images" :key="idx" class="dot" :class="{ active: idx === currentImageIndex }"></span>
        </div>
      </div>'''

c = c.replace(old_temp, new_temp)

# 2) Replace all image-related CSS
old_css_start = c.find('.detail-image {')
old_css_end = c.find('.img-dots {')
# Find the end of .img-dots block
dots_end = c.find('\n}', c.find('.img-dots {')) + 2
# Remove everything from .detail-image to end of .img-dots
# Actually let me just find and replace specific blocks

# Replace detail-image + image-wrapper + detail-el-image + nav + dots CSS
old_section = c[old_css_start:dots_end]

new_section = '''.detail-image {
  margin: 0 auto 20px;
  max-width: 900px;
}
.img-stage {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 200px;
  background: #f8f9fa;
  border-radius: var(--radius-md);
}
.detail-image .detail-el-image {
  max-width: 100%;
  max-height: 600px;
  border-radius: var(--radius-md);
  cursor: zoom-in;
}
.img-nav-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  background: rgba(255,255,255,0.9);
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #333;
  z-index: 10;
  transition: all 0.2s;
}
.img-nav-btn:hover {
  background: white;
  box-shadow: 0 4px 12px rgba(0,0,0,0.25);
}
.img-nav-left { left: -20px; }
.img-nav-right { right: -20px; }
.img-dots {
  display: flex;
  justify-content: center;
  gap: 6px;
  margin-top: 10px;
}
.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #ddd;
  cursor: pointer;
  transition: all 0.2s;
}
.dot.active {
  background: var(--primary);
  width: 20px;
  border-radius: 4px;
}
'''

c = c[:old_css_start] + new_section + c[dots_end:]

# 3) Add import for ArrowRight
if 'ArrowRight' not in c:
    c = c.replace('ArrowLeft, EditPen, Delete, Star, ChatDotSquare, Close, Plus, Promotion', 'ArrowLeft, ArrowRight, EditPen, Delete, Star, ChatDotSquare, Close, Plus, Promotion')

open('frontend/src/views/post/PostDetailView.vue', 'w', encoding='utf-8').write(c)
print('Done')