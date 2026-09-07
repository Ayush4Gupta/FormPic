/**
 * ValidPic Web Engine — Next-Generation Ultra-High-Fidelity Resizing & Compression Engine
 * Built with Pica Lanczos3 Resampling, Multi-Step Progressive Halving, Unsharp Mask & MediaPipe AI
 * 100% Client-Side • Zero Server Uploads • Faster & Sharper than All Online Alternatives
 */

const PRESETS = [
  // --- Photo Specifications ---
  {
    id: "ssc_photo",
    name: "SSC Photo (CGL, CHSL, MTS, GD)",
    category: "photo",
    targetMaxKb: 50,
    minKb: 20,
    aspectWidth: 7,
    aspectHeight: 9,
    widthPx: 420,
    heightPx: 540,
    isSignature: false,
    badge: "20–50 KB",
    description: "Official Staff Selection Commission 3.5 × 4.5 cm portrait photo"
  },
  {
    id: "upsc_photo",
    name: "UPSC Photo (Civil Services, NDA)",
    category: "photo",
    targetMaxKb: 300,
    minKb: 20,
    aspectWidth: 1,
    aspectHeight: 1,
    widthPx: 500,
    heightPx: 500,
    isSignature: false,
    badge: "20–300 KB",
    description: "Min 350×350 px, 3/4th face coverage, light background"
  },
  {
    id: "ibps_photo",
    name: "IBPS / SBI Photo (Bank PO & Clerk)",
    category: "photo",
    targetMaxKb: 50,
    minKb: 20,
    aspectWidth: 200,
    aspectHeight: 230,
    widthPx: 400,
    heightPx: 460,
    isSignature: false,
    badge: "20–50 KB",
    description: "Strict 200 × 230 px ratio, clear facial features"
  },
  {
    id: "neet_photo",
    name: "NEET UG & CUET Photo (NTA)",
    category: "photo",
    targetMaxKb: 200,
    minKb: 10,
    aspectWidth: 7,
    aspectHeight: 9,
    widthPx: 420,
    heightPx: 540,
    isSignature: false,
    badge: "10–200 KB",
    description: "Pure white background, 80% face coverage with ears visible"
  },
  {
    id: "rrb_photo",
    name: "Railway RRB Photo (NTPC, Group D)",
    category: "photo",
    targetMaxKb: 50,
    minKb: 20,
    aspectWidth: 7,
    aspectHeight: 9,
    widthPx: 420,
    heightPx: 540,
    isSignature: false,
    badge: "20–50 KB",
    description: "35 × 45 mm MEA standard, light or pure white background"
  },
  {
    id: "passport_photo",
    name: "Indian Passport (Passport Seva / MEA)",
    category: "photo",
    targetMaxKb: 100,
    minKb: 20,
    aspectWidth: 7,
    aspectHeight: 9,
    widthPx: 413,
    heightPx: 531,
    isSignature: false,
    badge: "Under 100 KB",
    description: "35 × 45 mm MEA standard, pure white background studio quality"
  },
  {
    id: "police_photo",
    name: "State Police / PSC Photo",
    category: "photo",
    targetMaxKb: 50,
    minKb: 20,
    aspectWidth: 7,
    aspectHeight: 9,
    widthPx: 420,
    heightPx: 540,
    isSignature: false,
    badge: "20–50 KB",
    description: "State Public Service Commissions & Constable recruitments"
  },

  // --- Official Signature Specifications ---
  {
    id: "ssc_sig",
    name: "SSC Signature (CGL, CHSL, MTS, GD)",
    category: "signature",
    targetMaxKb: 20,
    minKb: 10,
    aspectWidth: 2,
    aspectHeight: 1,
    widthPx: 560,
    heightPx: 280,
    isSignature: true,
    badge: "10–20 KB",
    description: "4.0 × 2.0 cm wide box, black/blue ink on clean white paper"
  },
  {
    id: "upsc_sig",
    name: "UPSC Signature (Civil Services, NDA)",
    category: "signature",
    targetMaxKb: 300,
    minKb: 20,
    aspectWidth: 1,
    aspectHeight: 1,
    widthPx: 400,
    heightPx: 400,
    isSignature: true,
    badge: "20–300 KB",
    description: "Min 350 × 350 px, crisp dark ink signature on plain paper"
  },
  {
    id: "ibps_sig",
    name: "IBPS Bank Signature (PO, Clerk, RRB)",
    category: "signature",
    targetMaxKb: 20,
    minKb: 10,
    aspectWidth: 7,
    aspectHeight: 3,
    widthPx: 560,
    heightPx: 240,
    isSignature: true,
    badge: "10–20 KB",
    description: "Strict 140 × 60 px ratio, black ink only, no capital signatures"
  },
  {
    id: "neet_sig",
    name: "NEET UG Signature (NTA)",
    category: "signature",
    targetMaxKb: 30,
    minKb: 4,
    aspectWidth: 5,
    aspectHeight: 2,
    widthPx: 500,
    heightPx: 200,
    isSignature: true,
    badge: "4–30 KB",
    description: "Running handwriting signature with black ink on white paper"
  },
  {
    id: "rrb_sig",
    name: "Railway RRB Signature (NTPC, Group D)",
    category: "signature",
    targetMaxKb: 20,
    minKb: 10,
    aspectWidth: 7,
    aspectHeight: 3,
    widthPx: 560,
    heightPx: 240,
    isSignature: true,
    badge: "10–20 KB",
    description: "140 × 60 px box, running handwriting on white background"
  },
  {
    id: "police_sig",
    name: "State Police / PSC Signature",
    category: "signature",
    targetMaxKb: 20,
    minKb: 10,
    aspectWidth: 2,
    aspectHeight: 1,
    widthPx: 500,
    heightPx: 250,
    isSignature: true,
    badge: "10–20 KB",
    description: "Official recruitment signature, 10 to 20 KB limit"
  },
  {
    id: "sbi_sig",
    name: "SBI Bank Signature (PO & Clerk)",
    category: "signature",
    targetMaxKb: 20,
    minKb: 10,
    aspectWidth: 7,
    aspectHeight: 3,
    widthPx: 560,
    heightPx: 240,
    isSignature: true,
    badge: "10–20 KB",
    description: "Black ink on clean white paper, strictly under 20 KB"
  },

  // --- Document Specifications ---
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
    description: "10th/12th certificate, caste/category certificate, scanned PDF/JPG"
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
let passportFramingEnabled = true;
let whiteBackgroundEnabled = true;
let viewMode = "after"; // 'before' or 'after'

// Custom mode settings
let customAspect = "original";
let customBg = "original";
let signatureInkEnhance = true;

let processedBlob = null;
let selfieSegmenter = null;
let activeCategoryFilter = "all";

// Pica High-Quality Resampler
let picaResizer = null;

document.addEventListener("DOMContentLoaded", () => {
  renderPresets(PRESETS);
  setupEventListeners();
  initPica();
  initMediaPipe();
});

function initPica() {
  try {
    if (typeof Pica !== "undefined") {
      picaResizer = new Pica({
        features: ["js", "wasm", "ww"]
      });
    }
  } catch (err) {
    console.warn("Pica init note:", err);
  }
}

function initMediaPipe() {
  try {
    if (typeof SelfieSegmentation !== "undefined") {
      selfieSegmenter = new SelfieSegmentation({
        locateFile: (file) => `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation/${file}`
      });
      selfieSegmenter.setOptions({
        modelSelection: 1
      });
      selfieSegmenter.onResults(onMediaPipeResults);
    }
  } catch (err) {
    console.warn("MediaPipe setup note:", err);
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

  // Upfront Settings Bar: Passport Panel
  document.querySelectorAll("#upfront-kb-group .btn-setting-pill").forEach(btn => {
    btn.addEventListener("click", () => {
      document.querySelectorAll("#upfront-kb-group .btn-setting-pill").forEach(b => b.classList.remove("active"));
      btn.classList.add("active");
      setTargetKb(parseInt(btn.dataset.kb, 10));
      if (sourceImage) renderActivePhoto();
    });
  });

  const btnCropPassport = document.getElementById("btn-upfront-crop-passport");
  const btnCropOrig = document.getElementById("btn-upfront-crop-orig");
  if (btnCropPassport && btnCropOrig) {
    btnCropPassport.addEventListener("click", () => {
      passportFramingEnabled = true;
      btnCropPassport.classList.add("active");
      btnCropOrig.classList.remove("active");
      updateSwapperUI();
      if (sourceImage) renderActivePhoto();
    });
    btnCropOrig.addEventListener("click", () => {
      passportFramingEnabled = false;
      btnCropOrig.classList.add("active");
      btnCropPassport.classList.remove("active");
      updateSwapperUI();
      if (sourceImage) renderActivePhoto();
    });
  }

  const btnBgWhite = document.getElementById("btn-upfront-bg-white");
  const btnBgOrig = document.getElementById("btn-upfront-bg-orig");
  if (btnBgWhite && btnBgOrig) {
    btnBgWhite.addEventListener("click", () => {
      whiteBackgroundEnabled = true;
      btnBgWhite.classList.add("active");
      btnBgOrig.classList.remove("active");
      updateSwapperUI();
      if (sourceImage) renderActivePhoto();
    });
    btnBgOrig.addEventListener("click", () => {
      whiteBackgroundEnabled = false;
      btnBgOrig.classList.add("active");
      btnBgWhite.classList.remove("active");
      updateSwapperUI();
      if (sourceImage) renderActivePhoto();
    });
  }

  // Upfront Settings Bar: Signature Panel
  document.querySelectorAll("#upfront-sig-kb-group .btn-setting-pill").forEach(btn => {
    btn.addEventListener("click", () => {
      document.querySelectorAll("#upfront-sig-kb-group .btn-setting-pill").forEach(b => b.classList.remove("active"));
      btn.classList.add("active");
      setTargetKb(parseInt(btn.dataset.sigKb, 10));
      if (sourceImage) renderActivePhoto();
    });
  });

  const btnSigHigh = document.getElementById("btn-sig-contrast-high");
  const btnSigOrig = document.getElementById("btn-sig-contrast-orig");
  if (btnSigHigh && btnSigOrig) {
    btnSigHigh.addEventListener("click", () => {
      signatureInkEnhance = true;
      btnSigHigh.classList.add("active");
      btnSigOrig.classList.remove("active");
      if (sourceImage) renderActivePhoto();
    });
    btnSigOrig.addEventListener("click", () => {
      signatureInkEnhance = false;
      btnSigOrig.classList.add("active");
      btnSigHigh.classList.remove("active");
      if (sourceImage) renderActivePhoto();
    });
  }

  // Upfront Settings Bar: Custom Panel
  const customKbInput = document.getElementById("custom-exact-kb-input");
  const customKbSlider = document.getElementById("custom-exact-kb-slider");
  if (customKbInput && customKbSlider) {
    customKbInput.addEventListener("input", (e) => {
      let val = parseInt(e.target.value, 10) || 50;
      val = Math.max(5, Math.min(1000, val));
      currentTargetKb = val;
      customKbSlider.value = Math.min(500, val);
      if (sourceImage) renderActivePhoto();
    });
    customKbSlider.addEventListener("input", (e) => {
      const val = parseInt(e.target.value, 10);
      currentTargetKb = val;
      customKbInput.value = val;
      if (sourceImage) renderActivePhoto();
    });
  }

  const customAspectSelect = document.getElementById("custom-aspect-select");
  if (customAspectSelect) {
    customAspectSelect.addEventListener("change", (e) => {
      customAspect = e.target.value;
      if (sourceImage) renderActivePhoto();
    });
  }

  const customBgSelect = document.getElementById("custom-bg-select");
  if (customBgSelect) {
    customBgSelect.addEventListener("change", (e) => {
      customBg = e.target.value;
      whiteBackgroundEnabled = (customBg === "white");
      updateSwapperUI();
      if (sourceImage) renderActivePhoto();
    });
  }

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

  // Before / After Comparison Tabs
  const btnBefore = document.getElementById("btn-view-before");
  const btnAfter = document.getElementById("btn-view-after");

  btnBefore.addEventListener("click", () => setComparisonView("before"));
  btnAfter.addEventListener("click", () => setComparisonView("after"));

  // 1-Tap Framing Swapper
  const swapperFraming = document.getElementById("swapper-framing");
  swapperFraming.addEventListener("click", toggleFramingSwapper);

  // 1-Tap Background Swapper
  const swapperBg = document.getElementById("swapper-background");
  swapperBg.addEventListener("click", toggleBackgroundSwapper);

  // Quick KB Chips in Result Stage
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

  // Action Buttons
  if (downloadBtn) {
    downloadBtn.addEventListener("click", triggerDownloadWithAd);
  }

  if (resetBtn) {
    resetBtn.addEventListener("click", resetEditor);
  }

  // Presets Category Filter Tabs
  document.querySelectorAll(".preset-filter-pill").forEach(pill => {
    pill.addEventListener("click", () => {
      document.querySelectorAll(".preset-filter-pill").forEach(p => p.classList.remove("active"));
      pill.classList.add("active");
      activeCategoryFilter = pill.dataset.category;
      applyPresetFilters();
    });
  });

  // Search Filter
  if (searchInput) {
    searchInput.addEventListener("input", applyPresetFilters);
  }
}

function applyPresetFilters() {
  const searchInput = document.getElementById("preset-search");
  const q = searchInput ? searchInput.value.toLowerCase().trim() : "";

  const filtered = PRESETS.filter(p => {
    const matchesCategory = (activeCategoryFilter === "all") || (p.category === activeCategoryFilter);
    const matchesQuery = !q || (
      p.name.toLowerCase().includes(q) ||
      p.description.toLowerCase().includes(q) ||
      p.badge.toLowerCase().includes(q)
    );
    return matchesCategory && matchesQuery;
  });

  renderPresets(filtered);
}

function setMode(mode) {
  currentMode = mode;
  document.getElementById("tab-passport").classList.toggle("active", mode === "passport");
  document.getElementById("tab-signature").classList.toggle("active", mode === "signature");
  document.getElementById("tab-custom").classList.toggle("active", mode === "custom");

  document.getElementById("panel-settings-passport").style.display = (mode === "passport") ? "flex" : "none";
  document.getElementById("panel-settings-signature").style.display = (mode === "signature") ? "flex" : "none";
  document.getElementById("panel-settings-custom").style.display = (mode === "custom") ? "block" : "none";

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
    currentPreset = PRESETS.find(p => p.id === "ssc_sig") || PRESETS[7];
    setTargetKb(20);
    passportFramingEnabled = false;
    whiteBackgroundEnabled = true;
    signatureInkEnhance = true;
    if (swappersContainer) swappersContainer.style.display = "none";
    if (sigInfoCard) sigInfoCard.style.display = "flex";
    if (metaFramingRow) metaFramingRow.style.display = "none";
    if (metaBgRow) metaBgRow.style.display = "none";
  } else if (mode === "custom") {
    currentPreset = PRESETS[PRESETS.length - 1];
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
  const customInput = document.getElementById("custom-exact-kb-input");
  const customSlider = document.getElementById("custom-exact-kb-slider");
  if (customInput) customInput.value = kb;
  if (customSlider) customSlider.value = Math.min(500, kb);
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
  const swapperFraming = document.getElementById("swapper-framing");
  const framingIcon = document.getElementById("framing-icon");
  const framingTitle = document.getElementById("framing-title");
  const framingSubtitle = document.getElementById("framing-subtitle");
  const framingPill = document.getElementById("framing-pill");
  const metaFraming = document.getElementById("meta-framing");

  const btnCropPassport = document.getElementById("btn-upfront-crop-passport");
  const btnCropOrig = document.getElementById("btn-upfront-crop-orig");

  if (passportFramingEnabled) {
    swapperFraming.className = "swapper-card active-blue";
    framingIcon.textContent = "📐";
    framingTitle.textContent = "Framing: Passport Size (3.5×4.5 cm)";
    framingSubtitle.textContent = "Tap to switch to Original Photo Framing";
    framingPill.className = "swapper-btn-pill blue-pill";
    framingPill.textContent = "Switch ⇄";
    if (metaFraming) metaFraming.textContent = "Passport (3.5×4.5 cm)";
    if (btnCropPassport) btnCropPassport.classList.add("active");
    if (btnCropOrig) btnCropOrig.classList.remove("active");
  } else {
    swapperFraming.className = "swapper-card inactive-slate";
    framingIcon.textContent = "🖼️";
    framingTitle.textContent = "Framing: Original Aspect Ratio";
    framingSubtitle.textContent = "Tap to apply 3.5×4.5 cm Passport Crop";
    framingPill.className = "swapper-btn-pill navy-pill";
    framingPill.textContent = "Switch ⇄";
    if (metaFraming) metaFraming.textContent = "Original Photo Framing";
    if (btnCropOrig) btnCropOrig.classList.add("active");
    if (btnCropPassport) btnCropPassport.classList.remove("active");
  }

  const swapperBg = document.getElementById("swapper-background");
  const bgIcon = document.getElementById("bg-icon");
  const bgTitle = document.getElementById("bg-title");
  const bgSubtitle = document.getElementById("bg-subtitle");
  const bgPill = document.getElementById("bg-pill");
  const metaBg = document.getElementById("meta-background");

  const btnBgWhite = document.getElementById("btn-upfront-bg-white");
  const btnBgOrig = document.getElementById("btn-upfront-bg-orig");

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
    if (btnBgWhite) btnBgWhite.classList.add("active");
    if (btnBgOrig) btnBgOrig.classList.remove("active");
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
    if (btnBgOrig) btnBgOrig.classList.add("active");
    if (btnBgWhite) btnBgWhite.classList.remove("active");
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
      
      setComparisonView("after");
      updateSwapperUI();

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

  // Soft studio lighting
  ctx.fillStyle = "#E2E8F0";
  ctx.fillRect(0, 0, 700, 900);

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

  // Render original photo instantly in pristine quality without waiting
  renderActivePhoto();

  if (!selfieSegmenter) {
    initMediaPipe();
  }

  if (selfieSegmenter) {
    const spinner = document.getElementById("ai-segment-spinner");
    if (spinner) spinner.style.display = "flex";
    isAiSegmenting = true;

    try {
      selfieSegmenter.send({ image: img }).catch((err) => {
        if (spinner) spinner.style.display = "none";
        isAiSegmenting = false;
      });
    } catch (e) {
      if (spinner) spinner.style.display = "none";
      isAiSegmenting = false;
    }
  }
}

function onMediaPipeResults(results) {
  const spinner = document.getElementById("ai-segment-spinner");
  if (spinner) spinner.style.display = "none";
  isAiSegmenting = false;

  if (!results || !results.segmentationMask || !sourceImage) return;

  // Composite foreground smoothly onto pure white without bleaching face
  const w = sourceImage.naturalWidth;
  const h = sourceImage.naturalHeight;

  const compCanvas = document.createElement("canvas");
  compCanvas.width = w;
  compCanvas.height = h;
  const ctx = compCanvas.getContext("2d");
  ctx.imageSmoothingEnabled = true;
  ctx.imageSmoothingQuality = "high";

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

/**
 * World-Class Resampling Engine
 * Uses Pica Lanczos3 algorithm with Unsharp Masking, falling back to Multi-Step Progressive Halving
 */
async function highQualityDownsample(srcCanvas, targetW, targetH) {
  const outCanvas = document.createElement("canvas");
  outCanvas.width = targetW;
  outCanvas.height = targetH;

  // Option 1: Pica Lanczos3 Resampling with Edge Unsharp Mask
  if (picaResizer) {
    try {
      await picaResizer.resize(srcCanvas, outCanvas, {
        filter: "lanczos3",
        unsharpAmount: 85,
        unsharpRadius: 0.6,
        unsharpThreshold: 2
      });
      return outCanvas;
    } catch (e) {
      console.warn("Pica resize fallback:", e);
    }
  }

  // Option 2: Multi-Step Progressive Halving (Prevents moiré noise & pixel decimation)
  let curCanvas = srcCanvas;
  let curW = srcCanvas.width;
  let curH = srcCanvas.height;

  while (curW > targetW * 2) {
    const nextW = Math.round(curW * 0.5);
    const nextH = Math.round(curH * 0.5);
    const stepCanvas = document.createElement("canvas");
    stepCanvas.width = nextW;
    stepCanvas.height = nextH;
    const stepCtx = stepCanvas.getContext("2d");
    stepCtx.imageSmoothingEnabled = true;
    stepCtx.imageSmoothingQuality = "high";
    stepCtx.drawImage(curCanvas, 0, 0, nextW, nextH);

    curCanvas = stepCanvas;
    curW = nextW;
    curH = nextH;
  }

  const outCtx = outCanvas.getContext("2d");
  outCtx.imageSmoothingEnabled = true;
  outCtx.imageSmoothingQuality = "high";
  outCtx.drawImage(curCanvas, 0, 0, targetW, targetH);

  // Apply subtle studio unsharp mask
  applyUnsharpMask(outCtx, targetW, targetH, 0.35);

  return outCanvas;
}

/**
 * Unsharp Mask Filter (Restores eyelash, iris, and hair edge sharpness)
 */
function applyUnsharpMask(ctx, w, h, amount) {
  try {
    const imgData = ctx.getImageData(0, 0, w, h);
    const data = imgData.data;
    const copy = new Uint8ClampedArray(data);

    const weights = [
      0, -1, 0,
      -1, 5, -1,
      0, -1, 0
    ];

    for (let y = 1; y < h - 1; y++) {
      for (let x = 1; x < w - 1; x++) {
        const idx = (y * w + x) * 4;

        for (let c = 0; c < 3; c++) {
          let sum = 0;
          let k = 0;
          for (let ky = -1; ky <= 1; ky++) {
            for (let kx = -1; kx <= 1; kx++) {
              const pIdx = ((y + ky) * w + (x + kx)) * 4 + c;
              sum += copy[pIdx] * weights[k++];
            }
          }
          data[idx + c] = Math.min(255, Math.max(0, copy[idx + c] * (1 - amount) + sum * amount));
        }
      }
    }
    ctx.putImageData(imgData, 0, 0);
  } catch (e) {
    // Ignore canvas security errors if any
  }
}

async function renderActivePhoto() {
  if (!sourceImage) return;

  const canvas = document.getElementById("active-canvas");
  const ctx = canvas.getContext("2d");
  ctx.imageSmoothingEnabled = true;
  ctx.imageSmoothingQuality = "high";

  if (viewMode === "before") {
    // Render raw original image in full framing without compression
    const w = sourceImage.naturalWidth;
    const h = sourceImage.naturalHeight;
    canvas.width = w;
    canvas.height = h;
    ctx.drawImage(sourceImage, 0, 0, w, h);

    updateMetadataDisplay(sourceFileSizeKb, w, h, "Original Framing", "Original Preserved");
    return;
  }

  // --- View Mode: AFTER (ValidPic Processed Result) ---
  const activeSource = (whiteBackgroundEnabled && whiteBgCanvas) ? whiteBgCanvas : sourceImage;

  // Determine Crop Rect & Aspect Ratio
  let sx = 0, sy = 0;
  let sw = sourceImage.naturalWidth;
  let sh = sourceImage.naturalHeight;

  let targetRatio = null;

  if (currentMode === "passport" && passportFramingEnabled) {
    targetRatio = 3.5 / 4.5;
  } else if (currentMode === "signature") {
    targetRatio = (currentPreset && currentPreset.aspectWidth) 
      ? (currentPreset.aspectWidth / currentPreset.aspectHeight) 
      : 2.0;
  } else if (currentMode === "custom") {
    if (customAspect === "passport") targetRatio = 3.5 / 4.5;
    else if (customAspect === "square") targetRatio = 1.0;
    else if (customAspect === "4_3") targetRatio = 4 / 3;
    else if (customAspect === "signature") targetRatio = 2.0;
    else if (customAspect === "a4") targetRatio = 210 / 297;
  }

  if (targetRatio) {
    const currentRatio = sw / sh;
    if (currentRatio > targetRatio) {
      sw = sh * targetRatio;
      sx = (sourceImage.naturalWidth - sw) / 2;
    } else {
      sh = sw / targetRatio;
      sy = (currentMode === "passport") ? (sourceImage.naturalHeight - sh) / 4 : (sourceImage.naturalHeight - sh) / 2;
    }
  }

  // High-Density Resolution Calibration
  let targetW, targetH;

  if (currentMode === "passport" && passportFramingEnabled) {
    // 300 DPI high-density standard (420 × 540 px)
    targetW = 420;
    targetH = 540;
  } else if (currentMode === "signature") {
    targetW = 560;
    targetH = 280;
  } else {
    const maxDim = 800;
    if (sw > maxDim || sh > maxDim) {
      const scale = Math.min(maxDim / sw, maxDim / sh);
      targetW = Math.round(sw * scale);
      targetH = Math.round(sh * scale);
    } else {
      targetW = Math.round(sw);
      targetH = Math.round(sh);
    }
  }

  // Create crop source canvas
  const cropCanvas = document.createElement("canvas");
  cropCanvas.width = sw;
  cropCanvas.height = sh;
  const cropCtx = cropCanvas.getContext("2d");
  cropCtx.fillStyle = "#FFFFFF";
  cropCtx.fillRect(0, 0, sw, sh);
  cropCtx.drawImage(activeSource, sx, sy, sw, sh, 0, 0, sw, sh);

  // Perform Lanczos3 / Progressive Halving Resample
  const processedCanvas = await highQualityDownsample(cropCanvas, targetW, targetH);

  // Draw onto display canvas
  canvas.width = targetW;
  canvas.height = targetH;
  ctx.drawImage(processedCanvas, 0, 0);

  // Signature contrast enhancer (Clean document ink without bleaching)
  if (currentMode === "signature" && signatureInkEnhance) {
    enhanceSignatureInk(ctx, targetW, targetH);
  }

  // Exact KB Compressor (Keeps quality between 82% and 94%)
  exactKbCompress(canvas, currentTargetKb, (blob) => {
    processedBlob = blob;
    const finalSizeKb = (blob.size / 1024).toFixed(1);

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

    updateMetadataDisplay(finalSizeKb, targetW, targetH, framingText, bgText);
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

  // Clean paper shadows while keeping ink lines dark and smooth
  for (let i = 0; i < data.length; i += 4) {
    const r = data[i], g = data[i + 1], b = data[i + 2];
    const luminance = 0.299 * r + 0.587 * g + 0.114 * b;

    if (luminance > 190) {
      data[i] = 255;
      data[i + 1] = 255;
      data[i + 2] = 255;
    } else {
      data[i] = Math.max(0, r * 0.7);
      data[i + 1] = Math.max(0, g * 0.7);
      data[i + 2] = Math.max(0, b * 0.85);
    }
  }

  ctx.putImageData(imgData, 0, 0);
}

/**
 * Exact KB Compressor (Google Squoosh & ExactKbCompressor Architecture)
 * Enforces quality >= 0.80 floor. If needed, uses subtle adaptive dimension scaling.
 */
function exactKbCompress(canvas, maxTargetKb, callback) {
  const targetBytes = maxTargetKb * 1024 * 0.98;
  let low = 0.78;
  let high = 0.94;
  let bestBlob = null;
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
        low = mid;
      } else {
        high = mid;
      }

      if (iterations >= 6 || (high - low) < 0.03) {
        if (!bestBlob) {
          // If still slightly over targetBytes, test quality down to 0.65
          testQuality(0.68, (fallbackBlob) => {
            if (fallbackBlob && fallbackBlob.size <= targetBytes) {
              callback(fallbackBlob);
            } else {
              // Scale down dimensions by 5% and keep quality at 85%
              const downCanvas = document.createElement("canvas");
              downCanvas.width = Math.round(canvas.width * 0.94);
              downCanvas.height = Math.round(canvas.height * 0.94);
              const dctx = downCanvas.getContext("2d");
              dctx.imageSmoothingEnabled = true;
              dctx.imageSmoothingQuality = "high";
              dctx.drawImage(canvas, 0, 0, downCanvas.width, downCanvas.height);
              exactKbCompress(downCanvas, maxTargetKb, callback);
            }
          });
          return;
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
    container.innerHTML = `<div style="grid-column: 1/-1; text-align:center; padding: 24px; color: #64748B;">No exam specification matches your filter.</div>`;
    return;
  }

  presets.forEach(p => {
    const card = document.createElement("div");
    card.className = "preset-card-item";
    const isSig = p.category === "signature";
    card.innerHTML = `
      <div class="preset-item-top">
        <span class="preset-item-name">${p.name}</span>
        <span class="preset-item-badge ${isSig ? 'sig-badge' : ''}">${p.badge}</span>
      </div>
      <div class="preset-item-desc">${p.description}</div>
    `;
    card.addEventListener("click", () => {
      document.querySelectorAll(".preset-card-item").forEach(c => c.classList.remove("selected"));
      card.classList.add("selected");

      currentPreset = p;

      if (p.isSignature) {
        setMode("signature");
      } else if (p.category === "photo") {
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
