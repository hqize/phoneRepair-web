import { ref, onMounted, type Ref } from 'vue'

export function useCaptcha(canvasRef: Ref<HTMLCanvasElement | null>) {
  const captchaText = ref('')

  function generate() {
    const canvas = canvasRef.value
    if (!canvas) return
    const ctx = canvas.getContext('2d')!
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
    const len = 5
    const arr = Array.from({ length: len }, () => chars[Math.floor(Math.random() * chars.length)])
    captchaText.value = arr.join('')

    canvas.width = 150
    canvas.height = 60
    ctx.fillStyle = '#f4f4f4'
    ctx.fillRect(0, 0, canvas.width, canvas.height)

    // 干扰线
    for (let i = 0; i < 5; i++) {
      ctx.strokeStyle = `rgba(${rand(256)},${rand(256)},${rand(256)},0.6)`
      ctx.beginPath()
      ctx.moveTo(Math.random() * canvas.width, Math.random() * canvas.height)
      ctx.lineTo(Math.random() * canvas.width, Math.random() * canvas.height)
      ctx.stroke()
    }

    // 字符
    arr.forEach((char, i) => {
      ctx.font = `${Math.random() * 10 + 30}px Arial`
      ctx.fillStyle = `rgba(${rand(256)},${rand(256)},${rand(256)},0.8)`
      const x = 20 + i * 25
      const y = 40 + Math.random() * 10
      const angle = Math.random() * Math.PI / 6 - Math.PI / 12
      ctx.save()
      ctx.translate(x, y)
      ctx.rotate(angle)
      ctx.fillText(char, 0, 0)
      ctx.restore()
    })

    // 干扰点
    for (let i = 0; i < 50; i++) {
      ctx.fillStyle = `rgba(${rand(256)},${rand(256)},${rand(256)},0.6)`
      ctx.beginPath()
      ctx.arc(Math.random() * canvas.width, Math.random() * canvas.height, 1, 0, Math.PI * 2)
      ctx.fill()
    }
  }

  onMounted(generate)
  return { captchaText, refresh: generate }
}

function rand(max: number) {
  return Math.floor(Math.random() * max)
}
