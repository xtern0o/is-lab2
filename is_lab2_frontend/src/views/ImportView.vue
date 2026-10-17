<template>
  <section class="import-view">
    <aside class="panel">
      <header>IMPORT HISTORY</header>
      <div class="empty-state">
        <strong>NO IMPORTS YET</strong>
        <span>completed operations will appear here</span>
      </div>
    </aside>

    <form class="panel" @submit.prevent>
      <header>NEW HOCON IMPORT</header>

      <div class="file-form">
        <p>Select a <strong>.conf</strong> or <strong>.hocon</strong> file.</p>
        <input type="file" accept=".conf,.hocon" />
        <button class="xp-button xp-button--primary" type="submit" disabled>
          IMPORT FILE
        </button>
      </div>
    </form>
  </section>
</template>

<style scoped>
.import-view {
  padding: 18px;
  display: grid;
  grid-template-columns: 1fr 3fr;
  gap: 16px;
}

.panel {
  min-height: 390px;
  border: 2px solid var(--ink);
  background: #fff9e8;
}

header {
  padding: 10px;
  color: #ffedcb;
  background: var(--burgundy);
  font: 800 10px/1 'Courier New', monospace;
}

.empty-state,
.file-form {
  min-height: 350px;
  padding: 24px;
  display: grid;
  place-content: center;
  justify-items: center;
  gap: 14px;
  color: #805a49;
  text-align: center;
  font: 700 10px/1.4 'Courier New', monospace;
}

.empty-state strong,
.file-form strong {
  color: var(--brick);
}

.file-form p {
  margin: 0;
}

@media (max-width: 760px) {
  .import-view {
    padding: 10px;
    grid-template-columns: 1fr;
  }

  .panel,
  .empty-state,
  .file-form {
    min-height: 180px;
  }
}
</style>