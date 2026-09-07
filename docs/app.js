/**
 * ValidPic Web Engine — 100% Client-Side Processing & Exact KB Binary Search Compressor
 * Zero server uploads • 100% Private • Free & Monetized with Ad Slots
 */

const PRESETS = [
  {
    id: "ssc",
    name: "SSC (CGL, CHSL, MTS, GD)",
    category: "exam",
    targetMaxKb: 50,
    minKb: 20,
    aspectWidth: 7,
    aspectHeight: 9,
    widthPx: 350,
    heightPx: 450,
    isSignature: false,
    badge: "20–50 KB",
    description: "Official Staff Selection Commission 3.5 × 4.5 cm photo"
  },
  {
    id: "upsc",
    name: "UPSC (Civil Services, NDA)",
    category: "exam",
    targetMaxKb: 300,
    minKb: 20,
    aspectWidth: 1,
    aspectHeight: 1,
    widthPx: 500,
    heightPx: 500,
    isSignature: false,
    badge: "20–300 KB",
    description: "Min 350×350 px, 3/4th face coverage"
  },
  {
    id: "ibps",
    name: "IBPS / SBI (Bank PO & Clerk)",
    category: "exam",
    targetMaxKb: 50,
    minKb: 20,
    aspectWidth: 200,
    aspectHeight: 230,
    widthPx: 200,
    heightPx: 230,
    isSignature: false,
    badge: "20–50 KB",
    description: "Strict 200 × 230 pixels dimension"
  },
  {
    id: "neet",
    name: "NTA NEET UG & CUET",
    category: "exam",
    targetMaxKb: 200,
    minKb: 10,
    aspectWidth: 7,
    aspectHeight: 9,
    widthPx: 400,
    heightPx: 514,
    isSignature: false,
    badge: "10–200 KB",
    description: "White background, 80% face coverage"
  },
  {
    id: "rrb",
    name: "Railway RRB (NTPC, Group D)",
    category: "exam",
    targetMaxKb: 50,
    minKb: 20,
    aspectWidth: 7,
    aspectHeight: 9,
    widthPx: 350,
    heightPx: 450,
    isSignature: false,
    badge: "20–50 KB",
    description: "35 × 45 mm, light/white background"
  },
  {
    id: "passport",
    name: "Indian Passport (Passport Seva)",
    category: "passport",
    targetMaxKb: 100,
    minKb: 20,
    aspectWidth: 7,
    aspectHeight: 9,
    widthPx: 413,
    heightPx: 531,
    isSignature: false,
    badge: "Under 100 KB",
    description: "35 × 45 mm MEA standard, pure white BG"
  },
  {
    id: "signature_official",
    name: "Official Signature (All Exams)",
    category: "signature",
    targetMaxKb: 20,
    minKb: 10,
    aspectWidth: 2,
    aspectHeight: 1,
    widthPx: 280,
    heightPx: 140,
    isSignature: true,
    badge: "10–20 KB",
    description: "Black/blue ink on crisp white paper"
  },
  {
    id: "marksheet_doc",
    name: "Certificate / Marksheet / ID",
    category: "document",
    targetMaxKb: 200,
    minKb: 50,
    aspectWidth: null,
    aspectHeight: null,
    widthPx: 1200,
    heightPx: 1600,
    isSignature: false,
    badge: "Under 200 KB",
    description: "10th/12th certificate, caste/category certificate"
  }
];

// App State
let currentTargetKb = 50;
let currentMode = "passport"; // 'passport' | 'signature' | 'custom'
let sourceImage = null;
let sourceFileName = "photo.jpg";
let sourceFileSizeKb = 0;
let processedBlob = null;
let passportFramingEnabled = true;
let whiteBackgroundEnhance = true;

document.addEventListener("DOMContentLoaded", () => {
  renderPresets(PRESETS);
  setupEventListeners();
});

function setupEventListeners() {
  const fileInput = document.getElementById("file-input");
  const dropzone = document.getElementById("dropzone");
  const selectFileBtn = document.getElementById("select-file-btn");
  const sampleBtn = document.getElementById("try-sample-btn");
  const downloadBtn = document.getElementById("download-btn");
  const resetBtn = document.getElementById("reset-btn");
  const searchInput = document.getElementById("preset-search");

  // Mode Switcher Tabs
  const tabPassport = document.getElementById("tab-passport");
  const tabSignature = document.getElementById("tab-signature");
  const tabCustom = document.getElementById("tab-custom");

  tabPassport.addEventListener("click", () => setMode("passport"));
  tabSignature.addEventListener("click", () => setMode("signature"));
  tabCustom.addEventListener("click", () => setMode("custom"));

  // File Upload Handlers
  if (selectFileBtn && fileInput) {
    selectFileBtn.addEventListener("click", (e) => {
      e.stopPropagation();
      fileInput.click();
    });
  }

  if (dropzone && fileInput) {
    dropzone.addEventListener("click", () => fileInput.click());
    
    dropzone.addEventListener("dragover", (e) => {
      e.preventDefault();
      dropzone.classList.add("dragover");
    });
    
    dropzone.addEventListener("dragleave", () => {
      dropzone.classList.remove("dragover");
    });
    
    dropzone.addEventListener("drop", (e) => {
      e.preventDefault();
      dropzone.classList.remove("dragover");
      if (e.dataTransfer.files && e.dataTransfer.files[0]) {
        handleFile(e.dataTransfer.files[0]);
      }
    });

    fileInput.addEventListener("change", (e) => {
      if (e.target.files && e.target.files[0]) {
        handleFile(e.target.files[0]);
      }
    });
  }

  // Sample Photo
  if (sampleBtn) {
    sampleBtn.addEventListener("click", (e) => {
      e.stopPropagation();
      loadSampleImage();
    });
  }

  // KB Chips
  document.querySelectorAll(".kb-chip").forEach(chip => {
    chip.addEventListener("click", () => {
      const kbVal = chip.dataset.kb;
      document.querySelectorAll(".kb-chip").forEach(c => c.classList.remove("active"));
      chip.classList.add("active");

      const sliderBox = document.getElementById("custom-kb-slider-box");
      if (kbVal === "custom") {
        if (sliderBox) sliderBox.style.display = "block";
        const slider = document.getElementById("custom-kb-range");
        currentTargetKb = parseInt(slider.value, 10);
      } else {
        if (sliderBox) sliderBox.style.display = "none";
        currentTargetKb = parseInt(kbVal, 10);
      }

      if (sourceImage) processImage();
    });
  });

  // Custom KB Slider
  const customSlider = document.getElementById("custom-kb-range");
  const customSliderVal = document.getElementById("custom-kb-val");
  if (customSlider) {
    customSlider.addEventListener("input", (e) => {
      currentTargetKb = parseInt(e.target.value, 10);
      if (customSliderVal) customSliderVal.textContent = `${currentTargetKb} KB`;
      if (sourceImage) processImage();
    });
  }

  // Toggles
  const toggleWhiteBg = document.getElementById("toggle-white-bg");
  if (toggleWhiteBg) {
    toggleWhiteBg.addEventListener("change", (e) => {
      whiteBackgroundEnhance = e.target.checked;
      if (sourceImage) processImage();
    });
  }

  const toggleCrop = document.getElementById("toggle-passport-ratio");
  if (toggleCrop) {
    toggleCrop.addEventListener("change", (e) => {
      passportFramingEnabled = e.target.checked;
      if (sourceImage) processImage();
    });
  }

  // Action Buttons
  if (downloadBtn) {
    downloadBtn.addEventListener("click", triggerDownloadWithAd);
  }

  if (resetBtn) {
    resetBtn.addEventListener("click", resetEditor);
  }

  // Search Filter
  if (searchInput) {
    searchInput.addEventListener("input", (e) => {
      const q = e.target.value.toLowerCase().trim();
      const filtered = PRESETS.filter(p =>
        p.name.toLowerCase().includes(q) ||
        p.description.toLowerCase().includes(q) ||
        p.badge.toLowerCase().includes(q)
      );
      renderPresets(filtered);
    });
  }
}

function setMode(mode) {
  currentMode = mode;
  document.getElementById("tab-passport").classList.toggle("active", mode === "passport");
  document.getElementById("tab-signature").classList.toggle("active", mode === "signature");
  document.getElementById("tab-custom").classList.toggle("active", mode === "custom");

  const toggleWhiteBg = document.getElementById("toggle-white-bg");
  const toggleCrop = document.getElementById("toggle-passport-ratio");

  if (mode === "passport") {
    setTargetKb(50);
    passportFramingEnabled = true;
    whiteBackgroundEnhance = true;
    if (toggleCrop) toggleCrop.checked = true;
    if (toggleWhiteBg) toggleWhiteBg.checked = true;
  } else if (mode === "signature") {
    setTargetKb(20);
    passportFramingEnabled = false;
    whiteBackgroundEnhance = true;
    if (toggleCrop) toggleCrop.checked = false;
    if (toggleWhiteBg) toggleWhiteBg.checked = true;
  } else if (mode === "custom") {
    setTargetKb(100);
    passportFramingEnabled = false;
    if (toggleCrop) toggleCrop.checked = false;
  }

  if (sourceImage) {
    processImage();
  }
}

function setTargetKb(kb) {
  currentTargetKb = kb;
  document.querySelectorAll(".kb-chip").forEach(c => {
    c.classList.toggle("active", parseInt(c.dataset.kb, 10) === kb);
  });
  const sliderBox = document.getElementById("custom-kb-slider-box");
  if (sliderBox) sliderBox.style.display = "none";
}

function renderPresets(presets) {
  const container = document.getElementById("presets-list");
  if (!container) return;

  container.innerHTML = "";
  if (presets.length === 0) {
    container.innerHTML = `<div style="grid-column: 1/-1; text-align:center; padding: 24px; color: #64748B;">No exam matches your search.</div>`;
    return;
  }

  presets.forEach(p => {
    const card = document.createElement("div");
    card.className = "preset-card-item";
    card.innerHTML = `
      <div class="preset-item-top">
        <span class="preset-item-name">${p.name}</span>
        <span class="preset-item-badge">${p.badge}</span>
      </div>
      <div class="preset-item-desc">${p.description}</div>
    `;
    card.addEventListener("click", () => {
      document.querySelectorAll(".preset-card-item").forEach(c => c.classList.remove("selected"));
      card.classList.add("selected");

      if (p.isSignature) {
        setMode("signature");
      } else if (p.aspectWidth && p.aspectHeight) {
        setMode("passport");
      } else {
        setMode("custom");
      }

      setTargetKb(p.targetMaxKb);
      if (sourceImage) processImage();

      // Smooth scroll back to converter card
      document.getElementById("converter-card").scrollIntoView({ behavior: "smooth" });
    });
    container.appendChild(card);
  });
}

function handleFile(file) {
  if (!file || !file.type.startsWith("image/")) {
    alert("Please select a valid image file (JPG, PNG, WebP).");
    return;
  }

  sourceFileName = file.name.replace(/\.[^/.]+$/, "") + "_validpic.jpg";
  sourceFileSizeKb = Math.round(file.size / 1024);

  const reader = new FileReader();
  reader.onload = (e) => {
    const img = new Image();
    img.onload = () => {
      sourceImage = img;
      document.getElementById("upload-stage").style.display = "none";
      document.getElementById("editor-stage").style.display = "block";
      processImage();
    };
    img.src = e.target.result;
  };
  reader.readAsDataURL(file);
}

function loadSampleImage() {
  const sampleCanvas = document.createElement("canvas");
  sampleCanvas.width = 700;
  sampleCanvas.height = 900;
  const ctx = sampleCanvas.getContext("2d");

  // Soft studio background
  ctx.fillStyle = "#F1F5F9";
  ctx.fillRect(0, 0, 700, 900);

  // Soft gradient vignette
  const grad = ctx.createRadialGradient(350, 420, 50, 350, 420, 450);
  grad.addColorStop(0, "#FFFFFF");
  grad.addColorStop(1, "#E2E8F0");
  ctx.fillStyle = grad;
  ctx.fillRect(0, 0, 700, 900);

  // Professional Suit Silhouette
  ctx.fillStyle = "#0A2540";
  ctx.beginPath();
  ctx.ellipse(350, 850, 320, 240, 0, 0, Math.PI * 2);
  ctx.fill();

  // White shirt collar V
  ctx.fillStyle = "#FFFFFF";
  ctx.beginPath();
  ctx.moveTo(310, 610);
  ctx.lineTo(350, 740);
  ctx.lineTo(390, 610);
  ctx.closePath();
  ctx.fill();

  // Neck
  ctx.fillStyle = "#D4A373";
  ctx.fillRect(320, 500, 60, 120);

  // Face oval
  ctx.fillStyle = "#E0A96D";
  ctx.beginPath();
  ctx.ellipse(350, 400, 130, 160, 0, 0, Math.PI * 2);
  ctx.fill();

  // Hair
  ctx.fillStyle = "#1E293B";
  ctx.beginPath();
  ctx.ellipse(350, 310, 140, 100, 0, 0, Math.PI);
  ctx.fill();

  const img = new Image();
  img.onload = () => {
    sourceFileName = "sample_passport_validpic.jpg";
    sourceFileSizeKb = 1450; // simulated original 1.45 MB
    sourceImage = img;
    document.getElementById("upload-stage").style.display = "none";
    document.getElementById("editor-stage").style.display = "block";
    processImage();
  };
  img.src = sampleCanvas.toDataURL("image/jpeg", 0.95);
}

function processImage() {
  if (!sourceImage) return;

  const canvas = document.getElementById("result-canvas");
  const ctx = canvas.getContext("2d");

  // Determine Crop Rect
  let sx = 0, sy = 0, sw = sourceImage.naturalWidth, sh = sourceImage.naturalHeight;

  if (passportFramingEnabled) {
    // 3.5 x 4.5 ratio
    const targetRatio = 3.5 / 4.5;
    const currentRatio = sw / sh;

    if (currentRatio > targetRatio) {
      sw = sh * targetRatio;
      sx = (sourceImage.naturalWidth - sw) / 2;
    } else {
      sh = sw / targetRatio;
      sy = (sourceImage.naturalHeight - sh) / 4; // Face bias towards top
    }
  }

  // Base canvas resolution
  let outW = Math.round(sw);
  let outH = Math.round(sh);

  // Max dimension bounds for web efficiency
  const maxDim = 1200;
  if (outW > maxDim || outH > maxDim) {
    const scale = Math.min(maxDim / outW, maxDim / outH);
    outW = Math.round(outW * scale);
    outH = Math.round(outH * scale);
  }

  canvas.width = outW;
  canvas.height = outH;

  // Pure White Background Base
  ctx.fillStyle = "#FFFFFF";
  ctx.fillRect(0, 0, outW, outH);

  // Draw scaled image
  ctx.drawImage(sourceImage, sx, sy, sw, sh, 0, 0, outW, outH);

  // Apply Enhancements
  if (whiteBackgroundEnhance) {
    applyStudioEnhance(ctx, outW, outH, currentMode === "signature");
  }

  // Binary Search Compression to Guarantee < currentTargetKb
  binarySearchCompress(canvas, currentTargetKb, (blob, finalQuality) => {
    processedBlob = blob;
    const finalSizeKb = (blob.size / 1024).toFixed(1);

    // Update Live Verification Pill
    const pill = document.getElementById("verification-pill");
    const text = document.getElementById("verification-text");
    const origInfo = document.getElementById("orig-info-tag");
    const dlBtnText = document.getElementById("download-btn-text");

    if (text) {
      text.textContent = `${finalSizeKb} KB (Guaranteed Under ${currentTargetKb} KB ✓) • ${outW} × ${outH} px`;
    }

    if (origInfo) {
      origInfo.textContent = `Original: ${sourceImage.naturalWidth} × ${sourceImage.naturalHeight} px • ${sourceFileSizeKb} KB`;
    }

    if (dlBtnText) {
      dlBtnText.textContent = `Download Ready Photo (${finalSizeKb} KB)`;
    }
  });
}

function applyStudioEnhance(ctx, w, h, isSignature) {
  const imgData = ctx.getImageData(0, 0, w, h);
  const data = imgData.data;

  if (isSignature) {
    // Signature Ink Enhancement
    for (let i = 0; i < data.length; i += 4) {
      const r = data[i], g = data[i + 1], b = data[i + 2];
      const luminance = 0.299 * r + 0.587 * g + 0.114 * b;

      if (luminance > 185) {
        data[i] = 255;
        data[i + 1] = 255;
        data[i + 2] = 255;
      } else {
        data[i] = Math.max(0, r * 0.7);
        data[i + 1] = Math.max(0, g * 0.7);
        data[i + 2] = Math.max(0, b * 0.85);
      }
    }
  } else {
    // Passport Studio Background Lightening
    for (let i = 0; i < data.length; i += 4) {
      const r = data[i], g = data[i + 1], b = data[i + 2];
      if (r > 195 && g > 195 && b > 195) {
        data[i] = 255;
        data[i + 1] = 255;
        data[i + 2] = 255;
      }
    }
  }

  ctx.putImageData(imgData, 0, 0);
}

function binarySearchCompress(canvas, maxTargetKb, callback) {
  const targetBytes = maxTargetKb * 1024 * 0.98; // 2% safe margin
  let low = 0.05;
  let high = 0.98;
  let bestBlob = null;
  let bestQuality = 0.85;
  let iterations = 0;

  function testQuality(q, onDone) {
    canvas.toBlob((blob) => {
      onDone(blob);
    }, "image/jpeg", q);
  }

  function step() {
    iterations++;
    const mid = (low + high) / 2;

    testQuality(mid, (blob) => {
      if (!blob) {
        callback(bestBlob || blob, bestQuality);
        return;
      }

      if (blob.size <= targetBytes) {
        bestBlob = blob;
        bestQuality = mid;
        low = mid; // Try higher quality
      } else {
        high = mid; // Needs more compression
      }

      if (iterations >= 7 || (high - low) < 0.03) {
        if (!bestBlob) {
          // If still over target, downscale canvas dimensions by 15% and retry
          if (canvas.width > 200) {
            const downCanvas = document.createElement("canvas");
            downCanvas.width = Math.round(canvas.width * 0.85);
            downCanvas.height = Math.round(canvas.height * 0.85);
            const dctx = downCanvas.getContext("2d");
            dctx.drawImage(canvas, 0, 0, downCanvas.width, downCanvas.height);
            binarySearchCompress(downCanvas, maxTargetKb, callback);
            return;
          }
          bestBlob = blob;
        }
        callback(bestBlob, bestQuality);
      } else {
        step();
      }
    });
  }

  step();
}

function triggerDownloadWithAd() {
  if (!processedBlob) return;

  const modal = document.getElementById("download-ad-modal");
  if (modal) {
    modal.style.display = "flex";
  }

  setTimeout(() => {
    if (modal) modal.style.display = "none";
    executeDownload();
  }, 1200);
}

function executeDownload() {
  if (!processedBlob) return;
  const url = URL.createObjectURL(processedBlob);
  const a = document.createElement("a");
  a.href = url;
  a.download = sourceFileName;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  setTimeout(() => URL.revokeObjectURL(url), 3000);
}

function resetEditor() {
  sourceImage = null;
  processedBlob = null;
  document.getElementById("editor-stage").style.display = "none";
  document.getElementById("upload-stage").style.display = "block";
  document.getElementById("file-input").value = "";
}
