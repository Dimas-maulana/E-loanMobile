# Loan Simulation Debugging Guide

## Changes Made

We've made several changes to fix the tenor selection issue in the Loan Simulation UI:

### 1. **Enhanced Logging**
Added comprehensive Timber logs throughout `LoanSimulationViewModel.kt` to track:
- Amount changes (raw and parsed values)
- Local plafond detection results
- MaxTenor values (raw from API and effective values)
- Tenor adjustments
- Simulation calculations

### 2. **MaxTenor Logic**
- **Raw MaxTenor = 0**: Treated as 60 months (unlimited/standard maximum)
- **Raw MaxTenor > 0**: Used as-is
- This logic is applied in both:
  - `detectLocalPlafond()` for instant local updates
  - `triggerSimulation()` for API-based updates
  - UI display (LoanSimulationScreen.kt)

### 3. **Local Plafond Detection**
- Fetches all plafonds on ViewModel init
- Instantly detects matching product when amount changes
- Updates `selectedPlafond` immediately for responsive UI
- Falls back to API if local cache is empty

## How to Debug

### Step 1: Monitor Logcat
Filter by tag: `LoanSim`

Look for these log patterns when you move the amount slider:

```
===== AmountChanged Event =====
Raw amount: 25.000.000, Parsed: 25000000
Local Detection for 25000000 -> Product: Gold, RawMaxTenor: 0
Detected plafond maxTenor logic -> raw: 0, effective: 60, currentTenor: 6
Tenor 6 is within limit 60, no adjustment needed
After detectLocalPlafond -> Plafond: Gold, MaxTenor: 0, CurrentTenor: 6
After calculateLocalSimulation -> Monthly: 437500.0
===== End AmountChanged =====
```

### Step 2: Check for Issues

**Issue 1: AllPlafonds Empty**
```
LoanSim: allPlafonds empty, will rely on API detection
```
**Solution**: Check network connectivity and API endpoint `/api/loan/plafonds`

**Issue 2: No Matching Plafond**
```
LoanSim: No matching plafond found for amount 500000
```
**Solution**: Verify the amount ranges in your plafond products match the slider range

**Issue 3: Tenor Auto-Adjustment**
```
LoanSim: Auto-adjusted tenor from 12 to 6 (max: 6)
```
**Expected**: This shows the system is correctly limiting tenor to maxTenor

### Step 3: Verify UI Behavior

1. **Move the amount slider** - Watch for:
   - "Product X is available for your loan amount. Maximum tenor: Y months." message
   - EstimatedInstallment updates instantly
   - Tenor chips enable/disable based on maxTenor

2. **Try to click tenor chips** - Watch for:
   - Enabled chips (darker background, alpha 1.0) should be clickable
   - Disabled chips (lighter background, alpha 0.5) should not respond
   - TenorChanged log should appear when clicking enabled chips

3. **Check specific amounts**:
   - 5,000,000 → Should detect Bronze (maxTenor: 6)
   - 15,000,000 → Should detect Silver (maxTenor: 12)  
   - 30,000,000 → Should detect Gold (maxTenor: 0 → effective 60)
   - 60,000,000 → Should detect Platinum (maxTenor: 0 → effective 60)
   - 100,000,000 → Should detect Diamond (maxTenor: 0 → effective 60)

## Expected Log Flow

### On App Start:
```
LoanSim: Loaded 5 plafonds
LoanSim: Product Bronze Range: 1000000-10000000 MaxTenor: 6
LoanSim: Product Silver Range: 10000001-25000000 MaxTenor: 12
LoanSim: Product Gold Range: 25000001-50000000 MaxTenor: 0
LoanSim: Product Platinum Range: 50000001-75000000 MaxTenor: 0
LoanSim: Product Diamond Range: 75000001-100000000 MaxTenor: 0
```

### On Slider Movement to 25,000,000:
```
===== AmountChanged Event =====
LoanSim: Raw amount: 25.000.000, Parsed: 25000000
LoanSim: Local Detection for 25000000 -> Product: Silver, RawMaxTenor: 12
LoanSim: Detected plafond maxTenor logic -> raw: 12, effective: 12, currentTenor: 6
LoanSim: Tenor 6 is within limit 12, no adjustment needed
LoanSim: After detectLocalPlafond -> Plafond: Silver, MaxTenor: 12, CurrentTenor: 6
LoanSim: After calculateLocalSimulation -> Monthly: [calculated value]
===== End AmountChanged =====
```

### On Slider Movement to 26,000,000 (crosses into Gold):
```
===== AmountChanged Event =====
LoanSim: Raw amount: 26.000.000, Parsed: 26000000
LoanSim: Local Detection for 26000000 -> Product: Gold, RawMaxTenor: 0
LoanSim: Detected plafond maxTenor logic -> raw: 0, effective: 60, currentTenor: 6
LoanSim: Tenor 6 is within limit 60, no adjustment needed
LoanSim: After detectLocalPlafond -> Plafond: Gold, MaxTenor: 0, CurrentTenor: 6
===== End AmountChanged =====
```

### On Clicking Tenor Chip (e.g., 12 months):
```
===== TenorChanged Event =====
LoanSim: New tenor: 12
===== End TenorChanged =====
```

## Troubleshooting

### Tenor Chips Still Grayed Out

**Possible Causes:**
1. `selectedPlafond` is null → Check "allPlafonds empty" or "No matching plafond" logs
2. UI not recomposing → Check if StateFlow is being observed correctly
3. `effectiveMaxTenor` calculation in UI is wrong → Check line 304-305 in LoanSimulationScreen.kt

**Debug Commands:**
```kotlin
// Add temporary log in LoanSimulationScreen.kt around line 310
Log.d("LoanSimUI", "TenorChip $tenor -> isEnabled: $isEnabled, effectiveMax: $effectiveMaxTenor, selected: ${state.selectedPlafond?.name}")
```

### Installment Shows 0 Rupiah

**Possible Causes:**
1. `interestRate` is 0 or null
2. `simulationResult` is null
3. Calculation error in `calculateLocalSimulation()`

**Check logs for:**
```
After calculateLocalSimulation -> Monthly: 0.0
```

If monthly is 0, the calculation failed. Check earlier logs for the plafond detection.

## Next Steps

1. **Run the app** with the changes
2. **Open Logcat** and filter by "LoanSim"
3. **Move the slider** and observe the logs
4. **Report findings**: Share the log output showing what happens when you move the slider

This will help us identify exactly where the issue is occurring.
