<script setup lang="ts">
import americanRun from '@/assets/img/american-run.gif'
import eagle from '@/assets/img/eagle-15084_256.gif'
</script>

<template>
  <footer class="footer">
    <div class="footer-icons" aria-hidden="true">
      <span><img :src="eagle" alt="" /></span>
      <span><img :src="americanRun" alt="" /></span>
      <span><img :src="eagle" alt="" /></span>
      <span><img :src="americanRun" alt="" /></span>
    </div>
    <div class="footer-copy"><strong>@maxkarn (c) 2026</strong></div>
    <div class="footer-icons" aria-hidden="true">
      <span><img :src="americanRun" alt="" /></span>
      <span><img :src="eagle" alt="" /></span>
      <span><img :src="americanRun" alt="" /></span>
      <span><img :src="eagle" alt="" /></span>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  min-height: 100px;
  padding: 10px 22px;
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  gap: 20px;
  background: var(--cream);
}

.footer-icons {
  display: flex;
  gap: 12px;
}

.footer-icons:last-child {
  justify-content: flex-end;
}

.footer-icons span {
  width: 76px;
  height: 76px;
}

.footer-icons img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.footer-copy {
  text-align: center;
  font-family: 'Courier New', monospace;
}

.footer-copy strong {
  display: block;
  color: var(--brick);
  font-size: 12px;
}

@media (max-width: 760px) {
  .footer {
    grid-template-columns: 1fr;
    padding: 18px;
  }

  .footer-icons,
  .footer-icons:last-child {
    justify-content: center;
  }

  .footer-icons span {
    width: 58px;
    height: 58px;
  }
}
</style>
