<script setup>
import { onMounted, onUnmounted, ref } from 'vue'

const fortune = ref('')
const timesSeen = ref(0)
const connected = ref(false)
let source

onMounted(() => {
  source = new EventSource(import.meta.env.VITE_FORTUNE_URL || '/fortune')

  source.addEventListener('open', () => {
    connected.value = true
  })

  source.addEventListener('fortune', (event) => {
    const data = JSON.parse(event.data)
    fortune.value = data.message
    timesSeen.value = data.timesSeen
  })

  source.addEventListener('error', () => {
    connected.value = false
  })
})

onUnmounted(() => {
  source?.close()
})
</script>

<template>
  <main class="fortune-page">
    <Transition name="fortune" mode="out-in">
      <p v-if="!fortune" class="loading" :class="{ disconnected: !connected }">
        Listening for a fortune...
      </p>
      <blockquote v-else :key="fortune" class="fortune" aria-live="polite">
        {{ fortune }}
        <footer>Seen {{ timesSeen }} times</footer>
      </blockquote>
    </Transition>
    <footer class="credits">
      Fortune content provided by
      <a href="/THIRD-PARTY-NOTICES.txt">fortune-mod and fortunes</a>.
    </footer>
  </main>
</template>
