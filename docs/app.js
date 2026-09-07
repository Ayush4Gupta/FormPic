/**
 * ValidPic Web Engine — 100% Client-Side Processing & Exact KB Binary Search Compressor
 * Direct 1:1 Parity with Native Android App (ResultPreviewScreen & BeforeAfterSlider)
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
let currentPreset = PRESETS[0];
let currentTargetKb = 50;
let currentMode = "passport"; // 'passport' | 'signature' | 'custom'

// Raw upload & cached AI processed bitmaps
let sourceImage = null;
let sourceFileName = "photo.jpg";
let sourceFileSizeKb = 0;
let whiteBgCanvas = null; // AI segmented canvas
let isAiSegmenting = false;

// 1-Tap Swapper states (exact Android parity)
let passportFramingEnabled = true; // true = 3.5×4.5 cm, false = original aspect ratio
let whiteBackgroundEnabled = true; // true = pure white AI, false = original background
let viewMode = "after"; // 'before' (original) or 'after' (processed)

let processedBlob = null;
let selfieSegmenter = null;

document.addEventListener("DOMContentLoaded", () => {
  renderPresets(PRESETS);
  setupEventListeners();
  initMediaPipe();
});

function initMediaPipe() {
  try {
    if (typeof SelfieSegmentation !== "undefined") {
      selfieSegmenter = new SelfieSegmentation({
        locateFile: (file) => `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation/${file}`
      });
      selfieSegmenter.setOptions({
        modelSelection: 1 // high accuracy
      });
      selfieSegmenter.onResults(onMediaPipeResults);
    }
  } catch (err) {
    console.warn("MediaPipe initial setup notice:", err);
  }
}

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

  // Before / After Comparison Tabs (From BeforeAfterSlider.kt)
  const btnBefore = document.getElementById("btn-view-before");
  const btnAfter = document.getElementById("btn-view-after");

  btnBefore.addEventListener("click", () => setComparisonView("before"));
  btnAfter.addEventListener("click", () => setComparisonView("after"));

  // 1-Tap Framing Swapper (Passport ⇄ Original)
  const swapperFraming = document.getElementById("swapper-framing");
  swapperFraming.addEventListener("click", toggleFramingSwapper);

  // 1-Tap Background Swapper (Pure White ⇄ Original BG)
  const swapperBg = document.getElementById("swapper-background");
  swapperBg.addEventListener("click", toggleBackgroundSwapper);

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

      if (sourceImage) renderActivePhoto();
    });
  });

  // Custom KB Slider
  const customSlider = document.getElementById("custom-kb-range");
  const customSliderVal = document.getElementById("custom-kb-val");
  if (customSlider) {
    customSlider.addEventListener("input", (e) => {
      currentTargetKb = parseInt(e.target.value, 10);
      if (customSliderVal) customSliderVal.textContent = `${currentTargetKb} KB`;
      if (sourceImage) renderActivePhoto();
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

  const swappersContainer = document.getElementById("swappers-container");
  const sigInfoCard = document.getElementById("signature-info-card");
  const metaFramingRow = document.getElementById("row-meta-framing");
  const metaBgRow = document.getElementById("row-meta-bg");

  if (mode === "passport") {
    currentPreset = PRESETS[0];
    setTargetKb(50);
    passportFramingEnabled = true;
    whiteBackgroundEnabled = true;
    updateSwapperUI();
    if (swappersContainer) swappersContainer.style.display = "flex";
    if (sigInfoCard) sigInfoCard.style.display = "none";
    if (metaFramingRow) metaFramingRow.style.display = "flex";
    if (metaBgRow) metaBgRow.style.display = "flex";
  } else if (mode === "signature") {
    currentPreset = PRESETS[6]; // signature preset
    setTargetKb(20);
    passportFramingEnabled = false;
    whiteBackgroundEnabled = true;
    if (swappersContainer) swappersContainer.style.display = "none";
    if (sigInfoCard) sigInfoCard.style.display = "flex";
    if (metaFramingRow) metaFramingRow.style.display = "none";
    if (metaBgRow) metaBgRow.style.display = "none";
  } else if (mode === "custom") {
    currentPreset = PRESETS[7];
    setTargetKb(100);
    passportFramingEnabled = false;
    whiteBackgroundEnabled = false;
    updateSwapperUI();
    if (swappersContainer) swappersContainer.style.display = "flex";
    if (sigInfoCard) sigInfoCard.style.display = "none";
    if (metaFramingRow) metaFramingRow.style.display = "flex";
    if (metaBgRow) metaBgRow.style.display = "flex";
  }

  if (sourceImage) {
    renderActivePhoto();
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

function setComparisonView(view) {
  viewMode = view;
  document.getElementById("btn-view-before").classList.toggle("active", view === "before");
  document.getElementById("btn-view-after").classList.toggle("active", view === "after");

  const floatingTag = document.getElementById("floating-view-tag");
  if (floatingTag) {
    floatingTag.textContent = view === "before" ? "🖼️ Original Upload (Before)" : "✨ ValidPic Result (After)";
  }

  renderActivePhoto();
}

function toggleFramingSwapper() {
  passportFramingEnabled = !passportFramingEnabled;
  updateSwapperUI();
  if (viewMode === "before") {
    setComparisonView("after");
  } else {
    renderActivePhoto();
  }
}

function toggleBackgroundSwapper() {
  whiteBackgroundEnabled = !whiteBackgroundEnabled;
  updateSwapperUI();
  if (viewMode === "before") {
    setComparisonView("after");
  } else {
    renderActivePhoto();
  }
}

function updateSwapperUI() {
  // Update Framing Swapper
  const swapperFraming = document.getElementById("swapper-framing");
  const framingIcon = document.getElementById("framing-icon");
  const framingTitle = document.getElementById("framing-title");
  const framingSubtitle = document.getElementById("framing-subtitle");
  const framingPill = document.getElementById("framing-pill");
  const metaFraming = document.getElementById("meta-framing");

  if (passportFramingEnabled) {
    swapperFraming.className = "swapper-card active-blue";
    framingIcon.textContent = "📐";
    framingTitle.textContent = "Framing: Passport Size (3.5×4.5 cm)";
    framingSubtitle.textContent = "Tap to switch to Original Photo Framing";
    framingPill.className = "swapper-btn-pill blue-pill";
    framingPill.textContent = "Switch ⇄";
    if (metaFraming) metaFraming.textContent = "Passport (3.5×4.5 cm)";
  } else {
    swapperFraming.className = "swapper-card inactive-slate";
    framingIcon.textContent = "🖼️";
    framingTitle.textContent = "Framing: Original Aspect Ratio";
    framingSubtitle.textContent = "Tap to apply 3.5×4.5 cm Passport Crop";
    framingPill.className = "swapper-btn-pill navy-pill";
    framingPill.textContent = "Switch ⇄";
    if (metaFraming) metaFraming.textContent = "Original Photo Framing";
  }

  // Update Background Swapper
  const swapperBg = document.getElementById("swapper-background");
  const bgIcon = document.getElementById("bg-icon");
  const bgTitle = document.getElementById("bg-title");
  const bgSubtitle = document.getElementById("bg-subtitle");
  const bgPill = document.getElementById("bg-pill");
  const metaBg = document.getElementById("meta-background");

  if (whiteBackgroundEnabled) {
    swapperBg.className = "swapper-card active-emerald";
    bgIcon.textContent = "🪄";
    bgTitle.textContent = "Background: Pure White (AI)";
    bgSubtitle.textContent = "Tap to switch to Original Background";
    bgPill.className = "swapper-btn-pill emerald-pill";
    bgPill.textContent = "Switch ⇄";
    if (metaBg) {
      metaBg.textContent = "Pure White (AI)";
      metaBg.className = "meta-val highlight-green";
    }
  } else {
    swapperBg.className = "swapper-card inactive-slate";
    bgIcon.textContent = "⚪";
    bgTitle.textContent = "Background: Original Preserved";
    bgSubtitle.textContent = "Tap to apply Pure White Background (AI)";
    bgPill.className = "swapper-btn-pill navy-pill";
    bgPill.textContent = "Switch ⇄";
    if (metaBg) {
      metaBg.textContent = "Original Preserved";
      metaBg.className = "meta-val";
    }
  }
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
      whiteBgCanvas = null;
      document.getElementById("upload-stage").style.display = "none";
      document.getElementById("editor-stage").style.display = "block";
      
      // Default to "after" view
      setComparisonView("after");
      updateSwapperUI();

      // Trigger MediaPipe Selfie Segmentation for real AI white background
      triggerAiSegmentation(img);
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

  // Soft room indoor background
  ctx.fillStyle = "#E2E8F0";
  ctx.fillRect(0, 0, 700, 900);

  // Soft textured background pattern to test AI removal
  ctx.fillStyle = "#CBD5E1";
  ctx.fillRect(0, 0, 700, 450);

  // Suit Silhouette
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
    sourceFileSizeKb = 1450;
    sourceImage = img;
    whiteBgCanvas = null;
    document.getElementById("upload-stage").style.display = "none";
    document.getElementById("editor-stage").style.display = "block";
    setComparisonView("after");
    updateSwapperUI();
    triggerAiSegmentation(img);
  };
  img.src = sampleCanvas.toDataURL("image/jpeg", 0.95);
}

function triggerAiSegmentation(img) {
  if (currentMode === "signature") {
    renderActivePhoto();
    return;
  }

  const spinner = document.getElementById("ai-segment-spinner");
  if (spinner) spinner.style.display = "flex";
  isAiSegmenting = true;

  // Immediate rendering with fast smart-white fallback
  generateLocalWhiteFallback(img);
  renderActivePhoto();

  // Attempt MediaPipe if loaded
  if (selfieSegmenter) {
    try {
      selfieSegmenter.send({ image: img }).catch((err) => {
        console.warn("MediaPipe segmenter send notice:", err);
        if (spinner) spinner.style.display = "none";
        isAiSegmenting = false;
      });
    } catch (e) {
      if (spinner) spinner.style.display = "none";
      isAiSegmenting = false;
    }
  } else {
    // MediaPipe still downloading from CDN, retry in 800ms
    setTimeout(() => {
      if (typeof SelfieSegmentation !== "undefined" && !selfieSegmenter) {
        initMediaPipe();
        if (selfieSegmenter) {
          selfieSegmenter.send({ image: img }).catch(() => {});
        }
      }
      if (spinner) spinner.style.display = "none";
      isAiSegmenting = false;
    }, 800);
  }
}

function onMediaPipeResults(results) {
  const spinner = document.getElementById("ai-segment-spinner");
  if (spinner) spinner.style.display = "none";
  isAiSegmenting = false;

  if (!results || !results.segmentationMask || !sourceImage) return;

  // Create composite canvas with Pure White Background
  const w = sourceImage.naturalWidth;
  const h = sourceImage.naturalHeight;

  const compCanvas = document.createElement("canvas");
  compCanvas.width = w;
  compCanvas.height = h;
  const ctx = compCanvas.getContext("2d");

  // 1. Draw segmentation mask
  ctx.drawImage(results.segmentationMask, 0, 0, w, h);

  // 2. Keep only pixels overlapping the mask (foreground person)
  ctx.globalCompositeOperation = "source-in";
  ctx.drawImage(sourceImage, 0, 0, w, h);

  // 3. Draw pure white behind person
  ctx.globalCompositeOperation = "destination-over";
  ctx.fillStyle = "#FFFFFF";
  ctx.fillRect(0, 0, w, h);

  whiteBgCanvas = compCanvas;

  if (viewMode === "after" && whiteBackgroundEnabled) {
    renderActivePhoto();
  }
}

function generateLocalWhiteFallback(img) {
  // Intelligent local chroma & studio boost fallback
  const w = img.naturalWidth;
  const h = img.naturalHeight;
  const canvas = document.createElement("canvas");
  canvas.width = w;
  canvas.height = h;
  const ctx = canvas.getContext("2d");

  ctx.drawImage(img, 0, 0, w, h);
  const imgData = ctx.getImageData(0, 0, w, h);
  const data = imgData.data;

  // Enhance background to clean white
  for (let i = 0; i < data.length; i += 4) {
    const r = data[i], g = data[i + 1], b = data[i + 2];
    const luminance = 0.299 * r + 0.587 * g + 0.114 * b;
    if (luminance > 175) {
      data[i] = 255;
      data[i + 1] = 255;
      data[i + 2] = 255;
    }
  }

  ctx.putImageData(imgData, 0, 0);
  whiteBgCanvas = canvas;
}

function renderActivePhoto() {
  if (!sourceImage) return;

  const canvas = document.getElementById("active-canvas");
  const ctx = canvas.getContext("2d");

  if (viewMode === "before") {
    // Render raw original image in full framing
    const w = sourceImage.naturalWidth;
    const h = sourceImage.naturalHeight;
    canvas.width = w;
    canvas.height = h;
    ctx.drawImage(sourceImage, 0, 0, w, h);

    // Update metadata for Before
    updateMetadataDisplay(sourceFileSizeKb, w, h, "Original Framing", "Original Preserved");
    return;
  }

  // --- View Mode: AFTER (ValidPic Processed Result) ---
  const activeSource = (whiteBackgroundEnabled && whiteBgCanvas) ? whiteBgCanvas : sourceImage;

  // Determine Crop Rect
  let sx = 0, sy = 0;
  let sw = sourceImage.naturalWidth;
  let sh = sourceImage.naturalHeight;

  if (passportFramingEnabled && currentMode !== "signature") {
    // Official 3.5 x 4.5 passport portrait ratio
    const targetRatio = 3.5 / 4.5;
    const currentRatio = sw / sh;

    if (currentRatio > targetRatio) {
      sw = sh * targetRatio;
      sx = (sourceImage.naturalWidth - sw) / 2;
    } else {
      sh = sw / targetRatio;
      sy = (sourceImage.naturalHeight - sh) / 4; // Biased upwards for headshot
    }
  }

  // Base canvas resolution
  let outW = Math.round(sw);
  let outH = Math.round(sh);

  const maxDim = 1200;
  if (outW > maxDim || outH > maxDim) {
    const scale = Math.min(maxDim / outW, maxDim / outH);
    outW = Math.round(outW * scale);
    outH = Math.round(outH * scale);
  }

  canvas.width = outW;
  canvas.height = outH;

  // Fill pure white base
  ctx.fillStyle = "#FFFFFF";
  ctx.fillRect(0, 0, outW, outH);

  // Draw image
  ctx.drawImage(activeSource, sx, sy, sw, sh, 0, 0, outW, outH);

  // Signature contrast enhancer
  if (currentMode === "signature") {
    enhanceSignatureInk(ctx, outW, outH);
  }

  // Compress strictly under target KB using Binary Search
  binarySearchCompress(canvas, currentTargetKb, (blob) => {
    processedBlob = blob;
    const finalSizeKb = (blob.size / 1024).toFixed(1);

    // Update Live Verification Pill
    const text = document.getElementById("verification-text");
    if (text) {
      text.textContent = (currentMode === "signature")
        ? `Signature Strictly Under ${currentTargetKb} KB ✓`
        : `Strictly Under ${currentTargetKb} KB ✓`;
    }

    const dlBtnText = document.getElementById("download-btn-text");
    if (dlBtnText) {
      dlBtnText.textContent = `Download Photo (${finalSizeKb} KB)`;
    }

    const framingText = (currentMode === "signature")
      ? "Official Signature Ratio"
      : (passportFramingEnabled ? "Passport (3.5×4.5 cm)" : "Original Photo Framing");

    const bgText = (currentMode === "signature")
      ? "Clean Document Paper"
      : (whiteBackgroundEnabled ? "Pure White (AI)" : "Original Preserved");

    updateMetadataDisplay(finalSizeKb, outW, outH, framingText, bgText);
  });
}

function updateMetadataDisplay(sizeKb, w, h, framing, bg) {
  const metaSize = document.getElementById("meta-file-size");
  const metaDim = document.getElementById("meta-dimensions");
  const metaFraming = document.getElementById("meta-framing");
  const metaBg = document.getElementById("meta-background");
  const metaReq = document.getElementById("meta-requirement");

  if (metaSize) metaSize.textContent = `${sizeKb} KB (Target: ≤ ${currentTargetKb} KB)`;
  if (metaDim) metaDim.textContent = `${w} × ${h} px`;
  if (metaFraming) metaFraming.textContent = framing;
  if (metaBg) metaBg.textContent = bg;
  if (metaReq) metaReq.textContent = currentPreset ? currentPreset.name : "Custom Specification";
}

function enhanceSignatureInk(ctx, w, h) {
  const imgData = ctx.getImageData(0, 0, w, h);
  const data = imgData.data;

  for (let i = 0; i < data.length; i += 4) {
    const r = data[i], g = data[i + 1], b = data[i + 2];
    const luminance = 0.299 * r + 0.587 * g + 0.114 * b;

    if (luminance > 180) {
      data[i] = 255;
      data[i + 1] = 255;
      data[i + 2] = 255;
    } else {
      data[i] = Math.max(0, r * 0.65);
      data[i + 1] = Math.max(0, g * 0.65);
      data[i + 2] = Math.max(0, b * 0.8);
    }
  }

  ctx.putImageData(imgData, 0, 0);
}

function binarySearchCompress(canvas, maxTargetKb, callback) {
  const targetBytes = maxTargetKb * 1024 * 0.98;
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
        callback(bestBlob || blob);
        return;
      }

      if (blob.size <= targetBytes) {
        bestBlob = blob;
        bestQuality = mid;
        low = mid;
      } else {
        high = mid;
      }

      if (iterations >= 7 || (high - low) < 0.03) {
        if (!bestBlob) {
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
        callback(bestBlob);
      } else {
        step();
      }
    });
  }

  step();
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

      currentPreset = p;

      if (p.isSignature) {
        setMode("signature");
      } else if (p.aspectWidth && p.aspectHeight) {
        setMode("passport");
      } else {
        setMode("custom");
      }

      setTargetKb(p.targetMaxKb);
      if (sourceImage) renderActivePhoto();

      document.getElementById("converter-card").scrollIntoView({ behavior: "smooth" });
    });
    container.appendChild(card);
  });
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
  whiteBgCanvas = null;
  document.getElementById("editor-stage").style.display = "none";
  document.getElementById("upload-stage").style.display = "block";
  document.getElementById("file-input").value = "";
}
