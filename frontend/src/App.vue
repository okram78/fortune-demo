<script setup>
import { onMounted, onUnmounted, ref } from 'vue'

const fortune = ref('')
const connected = ref(false)
let source

onMounted(() => {
  source = new EventSource(import.meta.env.VITE_FORTUNE_URL || '/fortune')

  source.addEventListener('open', () => {
    connected.value = true
  })

  source.addEventListener('fortune', (event) => {
    fortune.value = JSON.parse(event.data).message
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
    <p v-if="!fortune" class="loading" :class="{ disconnected: !connected }">
      Listening for a fortune...
    </p>
    <blockquote v-else class="fortune" aria-live="polite">
      {{ fortune }}
    </blockquote>
  </main>
</template>
