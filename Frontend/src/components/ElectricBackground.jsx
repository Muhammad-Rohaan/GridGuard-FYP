import { useEffect, useRef } from 'react';

export default function ElectricBackground() {
  const ref = useRef(null);
  useEffect(() => {
    const canvas = ref.current, ctx = canvas.getContext('2d');
    let raf, W, H, dpr, bolt = null, nextBolt = 2500;
    const nodes = Array.from({ length: 14 }, () => ({ x: Math.random(), y: Math.random(), p: Math.random() * 6.28 }));
    const resize = () => { dpr = window.devicePixelRatio || 1; W = canvas.clientWidth; H = canvas.clientHeight; canvas.width = W * dpr; canvas.height = H * dpr; };
    resize(); window.addEventListener('resize', resize);
    const makeBolt = () => { const pts = []; let x = W * (0.1 + Math.random() * 0.8), y = 0; pts.push({ x, y }); while (y < H * 0.55) { y += 20 + Math.random() * 30; x += (Math.random() - 0.5) * 60; pts.push({ x, y }); } return pts; };
    const draw = (ts) => {
      ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
      const g = ctx.createLinearGradient(0, 0, 0, H);
      g.addColorStop(0, '#04070d'); g.addColorStop(0.6, '#071120'); g.addColorStop(1, '#04070d');
      ctx.fillStyle = g; ctx.fillRect(0, 0, W, H);
      ctx.strokeStyle = 'rgba(34,211,238,0.05)'; ctx.lineWidth = 1;
      for (let x = 0; x < W; x += 60) { ctx.beginPath(); ctx.moveTo(x, 0); ctx.lineTo(x, H); ctx.stroke(); }
      for (let y = 0; y < H; y += 60) { ctx.beginPath(); ctx.moveTo(0, y); ctx.lineTo(W, y); ctx.stroke(); }
      nodes.forEach((n, i) => nodes.slice(i + 1).forEach((m) => {
        const dx = (n.x - m.x) * W, dy = (n.y - m.y) * H;
        if (Math.hypot(dx, dy) < 260) {
          ctx.strokeStyle = 'rgba(56,189,248,0.10)'; ctx.beginPath(); ctx.moveTo(n.x * W, n.y * H); ctx.lineTo(m.x * W, m.y * H); ctx.stroke();
          const f = ((ts * 0.00012) + i * 0.13) % 1;
          ctx.fillStyle = 'rgba(103,232,249,0.5)'; ctx.beginPath(); ctx.arc(n.x * W + dx * f, n.y * H + dy * f, 1.6, 0, 7); ctx.fill();
        }
      }));
      nodes.forEach((n) => { const pulse = 0.5 + 0.5 * Math.sin(ts * 0.002 + n.p); ctx.fillStyle = `rgba(34,211,238,${0.25 + 0.35 * pulse})`; ctx.beginPath(); ctx.arc(n.x * W, n.y * H, 2.2 + 1.6 * pulse, 0, 7); ctx.fill(); });
      ctx.beginPath();
      for (let x = 0; x <= W; x += 4) {
        const y = H * 0.8 + Math.sin(x * 0.012 + ts * 0.002) * 20 + Math.sin(x * 0.03 - ts * 0.001) * 8;
        x === 0 ? ctx.moveTo(x, y) : ctx.lineTo(x, y);
      }
      ctx.strokeStyle = 'rgba(34,211,238,0.35)'; ctx.lineWidth = 1.6; ctx.shadowColor = 'rgba(34,211,238,0.8)'; ctx.shadowBlur = 12; ctx.stroke(); ctx.shadowBlur = 0;
      nextBolt -= 16;
      if (nextBolt <= 0 && !bolt) { bolt = { t: 0, pts: makeBolt() }; nextBolt = 4000 + Math.random() * 4000; }
      if (bolt) {
        bolt.t += 16; const a = Math.max(0, 1 - bolt.t / 450);
        ctx.strokeStyle = `rgba(190,242,255,${a * 0.8})`; ctx.lineWidth = 1.4; ctx.shadowColor = 'rgba(34,211,238,0.9)'; ctx.shadowBlur = 16;
        ctx.beginPath(); bolt.pts.forEach((p, i) => (i ? ctx.lineTo(p.x, p.y) : ctx.moveTo(p.x, p.y))); ctx.stroke(); ctx.shadowBlur = 0;
        if (a <= 0) bolt = null;
      }
      raf = requestAnimationFrame(draw);
    };
    raf = requestAnimationFrame(draw);
    return () => { cancelAnimationFrame(raf); window.removeEventListener('resize', resize); };
  }, []);
  return <canvas ref={ref} className="auth-canvas" />;
}
