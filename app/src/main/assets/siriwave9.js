 (function () {
   function SiriWaveCustomCurve(opt) {
     opt = opt || {};
     this.controller = opt.controller;
     this.tick = 0;
     this.amplitude = opt.amplitude || 1;

     this.color = opt.color || [151, 115, 255];
     this.opacity = opt.opacity || 0.5;
     this.offset = opt.offset || 0;

     this.respawn();
   }

   SiriWaveCustomCurve.prototype.respawn = function () {
     this.seed = Math.random();
     this.open_class = 2 + (Math.random() * 3) | 0;
   };

   SiriWaveCustomCurve.prototype.equation = function (i) {
     const p = this.tick;
     const amp = this.controller.amplitude;
     const baseY = Math.sin(p + this.offset);
     const decay = Math.pow(1 / (1 + Math.pow(this.open_class * i, 2)), 2);
     const y = -1 * Math.abs(baseY) * amp * this.amplitude * this.controller.MAX * decay;
     if (Math.abs(y) < 0.001) this.respawn();
     return y;
   };
SiriWaveCustomCurve.prototype._draw = function (mirror) {
  this.tick += this.controller.speed * (1 - 0.5 * Math.sin(this.seed * Math.PI));
  const ctx = this.controller.ctx;
  ctx.beginPath();

  const width = this.controller.width;
  const height = this.controller.height;
  const xBase = width / 2 + (-width / 4 + this.seed * (width / 2));
  const yBase = height / 2;

  let i = -3;
  let xInit;
  while (i <= 3) {
    const x = xBase + i * width / 4;
    const y = yBase + mirror * this.equation(i);
    xInit = xInit || x;
    ctx.lineTo(x, y);
    i += 0.01;
  }

  const gradient = ctx.createLinearGradient(0, yBase, width, yBase);
  gradient.addColorStop(0, `rgba(${this.color.join(',')},${this.opacity})`);
  gradient.addColorStop(1, `rgba(0,172,172,${this.opacity})`);

  if (this.opacity <= 0.3) {
    // For the 2px stroke line effect
    ctx.lineWidth = 12;
    ctx.strokeStyle = gradient;
    ctx.stroke();
  } else {
    ctx.fillStyle = gradient;
    ctx.lineTo(xInit, yBase);
    ctx.closePath();
    ctx.fill();
  }
};
//   SiriWaveCustomCurve.prototype._draw = function (mirror) {
//     this.tick += this.controller.speed * (1 - 0.5 * Math.sin(this.seed * Math.PI));
//     const ctx = this.controller.ctx;
//     ctx.beginPath();
//
//     const width = this.controller.width;
//     const height = this.controller.height;
//     const xBase = width / 2 + (-width / 4 + this.seed * (width / 2));
//     const yBase = height / 2;
//
//     let i = -3;
//     let xInit;
//     while (i <= 3) {
//       const x = xBase + i * width / 4;
//       const y = yBase + mirror * this.equation(i);
//       xInit = xInit || x;
//       ctx.lineTo(x, y);
//       i += 0.01;
//     }
//
//     const gradient = ctx.createLinearGradient(0, yBase, width, yBase);
//     gradient.addColorStop(0, `rgba(${this.color.join(',')},${this.opacity})`);
//     gradient.addColorStop(1, `rgba(0,172,172,${this.opacity})`);
//
//     ctx.fillStyle = gradient;
//     ctx.lineTo(xInit, yBase);
//     ctx.closePath();
//     ctx.fill();
//   };

   SiriWaveCustomCurve.prototype.draw = function () {
     this._draw(-1);
     this._draw(1);
   };

   function SiriWaveCustom(opt) {
     opt = opt || {};
     this.tick = 0;
     this.run = false;

     this.ratio = opt.ratio || window.devicePixelRatio || 1;
     this.width = opt.width || 320; // Default width, will be overridden
     this.height = this.ratio * (opt.height || 100);
     this.MAX = this.height / 2;

     this.speed = opt.speed || 0.08;
     this.amplitude = opt.amplitude || 1;
     this._interpolation = {
       speed: this.speed,
       amplitude: this.amplitude
     };

     this.speedInterpolationSpeed = opt.speedInterpolationSpeed || 0.005;
     this.amplitudeInterpolationSpeed = opt.amplitudeInterpolationSpeed || 0.05;

     this.canvas = document.createElement('canvas');
     this.updateCanvasSize();

     this.container = opt.container || document.body;
     this.container.appendChild(this.canvas);

     this.ctx = this.canvas.getContext('2d');
     this.curves = [];

     const baseColors = [
       { color: [151, 115, 255], opacity: 0.4, offset: 0 },
       { color: [151, 115, 255], opacity: 0.6, offset: 0.3 },
       { color: [151, 115, 255], opacity: 1.0, offset: 0.6 },
       { color: [151, 115, 255], opacity: 1.0, offset: 0.9 },
         { color: [0, 172, 172],     opacity: 0.3, offset: 1.2 },   // New Wave 1
         { color: [151, 115, 255],   opacity: 0.2, offset: 1.5 }    // New Wave 2
     ];

     for (let cfg of baseColors) {
       this.curves.push(new SiriWaveCustomCurve({
         controller: this,
         color: cfg.color,
         opacity: cfg.opacity,
         offset: cfg.offset
       }));
     }

     if (opt.autostart) this.start();
   }

   SiriWaveCustom.prototype.updateCanvasSize = function () {
     this.canvas.width = this.width * this.ratio;
     this.canvas.height = this.height * this.ratio;
     this.canvas.style.width = this.width + 'px';
     this.canvas.style.height = (this.height / this.ratio) + 'px';
     this.MAX = this.height / 2;
   };

   SiriWaveCustom.prototype.setWidth = function (width) {
     this.width = parseInt(width, 10);
     this.updateCanvasSize();
   };

   SiriWaveCustom.prototype.setHeight = function (height) {
     this.height = parseInt(height, 10) * this.ratio;
     this.updateCanvasSize();
   };

   SiriWaveCustom.prototype._interpolate = function (propertyStr) {
     const increment = this[propertyStr + 'InterpolationSpeed'];
     if (Math.abs(this._interpolation[propertyStr] - this[propertyStr]) <= increment) {
       this[propertyStr] = this._interpolation[propertyStr];
     } else {
       this[propertyStr] += (this._interpolation[propertyStr] > this[propertyStr] ? increment : -increment);
     }
   };

   SiriWaveCustom.prototype._clear = function () {
     this.ctx.clearRect(0, 0, this.width * this.ratio, this.height * this.ratio);
   };

   SiriWaveCustom.prototype._draw = function () {
     for (let i = 0; i < this.curves.length; i++) {
       this.curves[i].draw();
     }
   };

   SiriWaveCustom.prototype._startDrawCycle = function () {
     if (!this.run) return;
     this._clear();
     this._interpolate('amplitude');
     this._interpolate('speed');
     this._draw();

     if (window.requestAnimationFrame) {
       requestAnimationFrame(this._startDrawCycle.bind(this));
     } else {
       setTimeout(this._startDrawCycle.bind(this), 20);
     }
   };

   SiriWaveCustom.prototype.start = function () {
     this.tick = 0;
     this.run = true;
     this._startDrawCycle();
   };

   SiriWaveCustom.prototype.stop = function () {
     this.run = false;
   };

   SiriWaveCustom.prototype.setSpeed = function (v) {
     this._interpolation.speed = v;
   };

   SiriWaveCustom.prototype.setAmplitude = function (v) {
  console.log("Amplitude set to:", v);
     this._interpolation.amplitude = Math.max(Math.min(v, 1), 0);
   };

   window.SiriWaveCustom = SiriWaveCustom;
 })();
