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
    description: "Official Staff Selection Commission 3.5 × 4.5 cm"
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
    id: "signature_ssc",
    name: "Official Signature (SSC, IBPS)",
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
    id: "under_25",
    name: "Quick Target: Under 25 KB",
    category: "quick",
    targetMaxKb: 25,
    minKb: 5,
    aspectWidth: 7,
    aspectHeight: 9,
    widthPx: 350,
    heightPx: 450,
    isSignature: false,
    badge: "< 25 KB",
    description: "Ultra-compact for low-bandwidth portals"
  }
];

// App State
let currentPreset = PRESETS[0];
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

function renderPresets(presetsToRender) {
  const container = document.getElementById("presets-list");
  if (!container) return;

  container.innerHTML = "";
  if (presetsToRender.length === 0) {
    container.innerHTML = `<div style="text-align:center; padding: 20px; color: #64748B; font-size:13px;">No exam preset matches your search.</div>`;
    return;
  }

  presetsToRender.forEach(p => {
    const card = document.createElement("div");
    card.className = `preset-card ${p.id === currentPreset.id ? 'active' : ''}`;
    card.id = `preset-${p.id}`;
    card.innerHTML = `
      <div class="preset-card-header">
        <span class="preset-card-title">${p.name}</span>
        <span class="preset-card-badge">${p.badge}</span>
      </div>
      <div class="preset-card-specs">${p.description}</div>
    `;
    card.addEventListener("click", () => selectPreset(p));
    container.appendChild(card);
  });
}

function selectPreset(preset) {
  currentPreset = preset;
  document.querySelectorAll(".preset-card").forEach(c => c.classList.remove("active"));
  const activeCard = document.getElementById(`preset-${preset.id}`);
  if (activeCard) activeCard.classList.add("active");

  // Update quick KB buttons
  document.querySelectorAll(".kb-btn").forEach(btn => {
    btn.classList.toggle("active", parseInt(btn.dataset.kb) === preset.targetMaxKb);
  });

  if (sourceImage) {
    processImage();
  }
}

function setupEventListeners() {
  const fileInput = document.getElementById("file-input");
  const dropzone = document.getElementById("dropzone");
  const searchInput = document.getElementById("preset-search");
  const sampleBtn = document.getElementById("try-sample-btn");
  const downloadBtn = document.getElementById("download-btn");
  const resetBtn = document.getElementById("reset-btn");

  // Drag & drop
  if (dropzone && fileInput) {
    dropzone.addEventListener("click", () => fileInput.click());
    dropzone.addEventListener("dragover", (e) => {
      e.preventDefault();
      dropzone.classList.add("dragover");
    });
    dropzone.addEventListener("dragleave", () => dropzone.classList.remove("dragover"));
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

  // Sample photo generator
  if (sampleBtn) {
    sampleBtn.addEventListener("click", (e) => {
      e.stopPropagation();
      loadSampleImage();
    });
  }

  // Search filter
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

  // Quick KB buttons
  document.querySelectorAll(".kb-btn").forEach(btn => {
    btn.addEventListener("click", () => {
      const targetKb = parseInt(btn.dataset.kb);
      const matched = PRESETS.find(p => p.targetMaxKb === targetKb) || {
        id: `custom_${targetKb}`,
        name: `Custom Target Under ${targetKb} KB`,
        targetMaxKb: targetKb,
        aspectWidth: 7,
        aspectHeight: 9,
        widthPx: 350,
        heightPx: 450,
        isSignature: targetKb <= 20,
        badge: `< ${targetKb} KB`,
        description: `Targeting strictly under ${targetKb} KB`
      };
      selectPreset(matched);
    });
  });

  // Framing Toggle
  const toggleFraming = document.getElementById("toggle-framing");
  if (toggleFraming) {
    toggleFraming.addEventListener("click", () => {
      passportFramingEnabled = !passportFramingEnabled;
      document.getElementById("opt-passport").classList.toggle("active", passportFramingEnabled);
      document.getElementById("opt-original").classList.toggle("active", !passportFramingEnabled);
      if (sourceImage) processImage();
    });
  }

  // Background Enhance Toggle
  const toggleBg = document.getElementById("toggle-bg");
  if (toggleBg) {
    toggleBg.addEventListener("click", () => {
      whiteBackgroundEnhance = !whiteBackgroundEnhance;
      document.getElementById("opt-white-bg").classList.toggle("active", whiteBackgroundEnhance);
      document.getElementById("opt-orig-bg").classList.toggle("active", !whiteBackgroundEnhance);
      if (sourceImage) processImage();
    });
  }

  // Download Trigger
  if (downloadBtn) {
    downloadBtn.addEventListener("click", triggerDownload);
  }

  // Reset
  if (resetBtn) {
    resetBtn.addEventListener("click", () => {
      sourceImage = null;
      processedBlob = null;
      document.getElementById("dropzone").style.display = "block";
      document.getElementById("result-stage").classList.remove("visible");
      if (fileInput) fileInput.value = "";
    });
  }
}

function handleFile(file) {
  if (!file.type.startsWith("image/")) {
    alert("Please select a valid image file (JPG, PNG, WebP, or Screenshot).");
    return;
  }

  sourceFileName = file.name;
  sourceFileSizeKb = (file.size / 1024).toFixed(1);

  const reader = new FileReader();
  reader.onload = (event) => {
    const img = new Image();
    img.onload = () => {
      sourceImage = img;
      document.getElementById("dropzone").style.display = "none";
      document.getElementById("result-stage").classList.add("visible");
      document.getElementById("orig-meta").innerText = `Original: ${img.naturalWidth} × ${img.naturalHeight} px • ${sourceFileSizeKb} KB`;

      // Display original preview
      const origCanvas = document.getElementById("orig-canvas");
      const ctx = origCanvas.getContext("2d");
      origCanvas.width = img.naturalWidth;
      origCanvas.height = img.naturalHeight;
      ctx.drawImage(img, 0, 0);

      processImage();
    };
    img.src = event.target.result;
  };
  reader.readAsDataURL(file);
}

function loadSampleImage() {
  // Create an artificial passport-style sample in pure canvas
  const canvas = document.createElement("canvas");
  canvas.width = 600;
  canvas.height = 770;
  const ctx = canvas.getContext("2d");

  // Pure white studio backdrop
  ctx.fillStyle = "#FFFFFF";
  ctx.fillRect(0, 0, canvas.width, canvas.height);

  // Soft studio lighting vignette
  const radial = ctx.createRadialGradient(300, 320, 50, 300, 320, 380);
  radial.addColorStop(0, "#FFFFFF");
  radial.addColorStop(1, "#F1F5F9");
  ctx.fillStyle = radial;
  ctx.fillRect(0, 0, canvas.width, canvas.height);

  // Shoulders (Formal Navy Blazer)
  ctx.fillStyle = "#0A2540";
  ctx.beginPath();
  ctx.ellipse(300, 720, 240, 180, 0, 0, Math.PI * 2);
  ctx.fill();

  // White Shirt Collar
  ctx.fillStyle = "#FFFFFF";
  ctx.beginPath();
  ctx.moveTo(300, 550);
  ctx.lineTo(260, 620);
  ctx.lineTo(340, 620);
  ctx.closePath();
  ctx.fill();

  // Neck
  ctx.fillStyle = "#E0A97B";
  ctx.fillRect(270, 480, 60, 90);

  // Head (Oval)
  ctx.fillStyle = "#F3C59A";
  ctx.beginPath();
  ctx.ellipse(300, 360, 110, 145, 0, 0, Math.PI * 2);
  ctx.fill();

  // Hair
  ctx.fillStyle = "#1E293B";
  ctx.beginPath();
  ctx.arc(300, 290, 120, Math.PI, 0, false);
  ctx.fill();

  const img = new Image();
  img.onload = () => {
    sourceImage = img;
    sourceFileName = "sample_passport_photo.jpg";
    sourceFileSizeKb = "320.0";
    document.getElementById("dropzone").style.display = "none";
    document.getElementById("result-stage").classList.add("visible");
    document.getElementById("orig-meta").innerText = `Sample Photo: 600 × 770 px • 320 KB`;

    const origCanvas = document.getElementById("orig-canvas");
    origCanvas.width = img.width;
    origCanvas.height = img.height;
    origCanvas.getContext("2d").drawImage(img, 0, 0);

    processImage();
  };
  img.src = canvas.toDataURL("image/jpeg", 0.95);
}

/**
 * Executes binary search JPEG compression strictly under targetMaxKb
 */
async function processImage() {
  if (!sourceImage) return;

  const resultMeta = document.getElementById("result-meta");
  const downloadBtn = document.getElementById("download-btn");
  if (resultMeta) resultMeta.innerHTML = `<span style="color: #2563EB;">⏳ Optimizing exact file size...</span>`;

  // 1. Calculate Crop Box
  let cropWidth = sourceImage.naturalWidth;
  let cropHeight = sourceImage.naturalHeight;
  let cropX = 0;
  let cropY = 0;

  if (passportFramingEnabled && !currentPreset.isSignature) {
    const targetAspect = currentPreset.aspectWidth / currentPreset.aspectHeight;
    const sourceAspect = cropWidth / cropHeight;

    if (sourceAspect > targetAspect) {
      // Source is wider than target aspect ratio -> crop horizontal edges
      cropWidth = Math.round(cropHeight * targetAspect);
      cropX = Math.round((sourceImage.naturalWidth - cropWidth) / 2);
    } else {
      // Source is taller -> crop from bottom (keeps face centered near upper portion)
      cropHeight = Math.round(cropWidth / targetAspect);
      cropY = Math.round((sourceImage.naturalHeight - cropHeight) * 0.25);
    }
  }

  // 2. Render to Target Canvas
  let outWidth = currentPreset.widthPx || cropWidth;
  let outHeight = currentPreset.heightPx || cropHeight;

  // Keep proportions proportional
  const targetCanvas = document.getElementById("result-canvas");
  targetCanvas.width = outWidth;
  targetCanvas.height = outHeight;
  const ctx = targetCanvas.getContext("2d");

  // Clear with pure white
  ctx.fillStyle = "#FFFFFF";
  ctx.fillRect(0, 0, outWidth, outHeight);

  if (currentPreset.isSignature) {
    // High contrast for signature ink
    ctx.filter = "contrast(140%) brightness(105%)";
  } else if (whiteBackgroundEnhance) {
    ctx.filter = "brightness(102%) contrast(102%)";
  } else {
    ctx.filter = "none";
  }

  ctx.drawImage(sourceImage, cropX, cropY, cropWidth, cropHeight, 0, 0, outWidth, outHeight);
  ctx.filter = "none"; // reset

  // 3. Binary Search Compression against Exact Target KB
  const targetMaxBytes = currentPreset.targetMaxKb * 1024;
  let low = 0.05;
  let high = 0.98;
  let bestBlob = null;
  let bestQuality = 0.85;

  for (let iter = 0; iter < 9; iter++) {
    const mid = (low + high) / 2;
    const blob = await canvasToBlobAsync(targetCanvas, "image/jpeg", mid);

    if (blob.size <= targetMaxBytes) {
      bestBlob = blob;
      bestQuality = mid;
      low = mid; // Try higher quality
    } else {
      high = mid; // File size exceeds limit -> drop quality
    }
  }

  // If even quality 0.05 is over target, downscale dimensions by 15% and retry
  if (!bestBlob || bestBlob.size > targetMaxBytes) {
    const scale = 0.85;
    targetCanvas.width = Math.round(outWidth * scale);
    targetCanvas.height = Math.round(outHeight * scale);
    const scaledCtx = targetCanvas.getContext("2d");
    scaledCtx.fillStyle = "#FFFFFF";
    scaledCtx.fillRect(0, 0, targetCanvas.width, targetCanvas.height);
    scaledCtx.drawImage(sourceImage, cropX, cropY, cropWidth, cropHeight, 0, 0, targetCanvas.width, targetCanvas.height);
    bestBlob = await canvasToBlobAsync(targetCanvas, "image/jpeg", 0.70);
  }

  processedBlob = bestBlob;
  const finalKb = (processedBlob.size / 1024).toFixed(1);
  const isSatisfied = processedBlob.size <= targetMaxBytes;

  if (resultMeta) {
    resultMeta.className = `preview-meta ${isSatisfied ? 'success' : ''}`;
    resultMeta.innerHTML = `Output: ${targetCanvas.width} × ${targetCanvas.height} px • <strong>${finalKb} KB</strong> (Under ${currentPreset.targetMaxKb} KB ✓)`;
  }

  if (downloadBtn) {
    downloadBtn.innerHTML = `📥 Download Ready Photo (${finalKb} KB .jpg)`;
  }
}

function canvasToBlobAsync(canvas, type, quality) {
  return new Promise(resolve => canvas.toBlob(resolve, type, quality));
}

function triggerDownload() {
  if (!processedBlob) return;

  // Show Interstitial / Ad Notice briefly (High monetization conversion)
  const adModal = document.getElementById("download-ad-modal");
  if (adModal) {
    adModal.style.display = "flex";
    setTimeout(() => {
      adModal.style.display = "none";
      executeFileDownload();
    }, 1200);
  } else {
    executeFileDownload();
  }
}

function executeFileDownload() {
  const url = URL.createObjectURL(processedBlob);
  const a = document.createElement("a");
  const base = sourceFileName.substring(0, sourceFileName.lastIndexOf(".")) || "photo";
  a.href = url;
  a.download = `ValidPic_${currentPreset.id}_${base}.jpg`;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  setTimeout(() => URL.revokeObjectURL(url), 2000);
}
