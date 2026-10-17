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
      <header>new HOCON (.conf) import</header>

      <div class="file-form">
        <p>выберите <strong>.conf</strong> или <strong>.hocon</strong> файл.</p>

        <details class="format-help">
          <summary>подробнее о формате hocon</summary>
          <p>
            принимается формат HOCON (.conf)!!
            <a
              href="https://github.com/lightbend/config/blob/main/HOCON.md"
              target="_blank"
              rel="noopener noreferrer"
            >
              дока
            </a>
          </p>
        </details>

        <input type="file" accept=".conf,.hocon" />
        <button
          class="xp-button xp-button--primary import-button"
          type="submit"
          disabled
        >
          import file
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

.import-button {
  font-size: 14px;
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
  font: 700 14px/1.4 'Courier New', monospace;
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

.format-help {
  color: var(--brick);
  font-size: 13px;
}

.format-help summary {
  cursor: pointer;
  font-size: 14px;
  font-weight: 800;
  text-decoration: underline;
}

.format-help p {
  margin: 8px 0 0;
  font-size: 12px;
}

.format-help a {
  color: var(--burgundy);
}
</style>