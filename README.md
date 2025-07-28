# Shamir Secret Sharing - Polynomial Root Analysis

This project implements a simplified version of Shamir's Secret Sharing algorithm that reads polynomial roots from JSON files, decodes Y values from different bases, and finds the constant term (secret) of the polynomial using Lagrange interpolation.

## 🎯 Problem Statement

Given an unknown polynomial of degree m:
```
f(x) = a_m x^m + a_{m-1} x^{m-1} + ... + a_1 x + c
```

The task is to find the constant term `c` (the secret) using polynomial roots provided in JSON format, where:
- Y values are encoded in different bases
- Need to decode the Y values first
- Use Lagrange interpolation to find the polynomial
- Extract the constant term by evaluating at x = 0

## ✨ Features

- **JSON Input Parsing**: Reads polynomial roots from JSON files
- **Multi-Base Decoding**: Supports bases 2, 3, 4, 6, 7, 8, 10, 12, 15, 16
- **Lagrange Interpolation**: Finds the polynomial that passes through all given points
- **Large Number Support**: Uses BigInteger for handling large values
- **No External Dependencies**: Uses only Java standard libraries
- **Error Handling**: Comprehensive validation and error reporting

## 📁 Project Structure

```
Hashira/
├── src/
│   ├── Main.java                 # Main demonstration class
│   └── Shamir/
│       └── ShamirAlgo.java      # Core algorithm implementation
├── test1.json                   # First test case
├── test2.json                   # Second test case
└── README.md                    # This file
```

## 🚀 Usage

### Compilation
```bash
javac -d . src/Shamir/*.java src/Main.java
```

### Running the Algorithm
```bash
# Run the main demonstration
java Main

# Run the algorithm directly
java Shamir.ShamirAlgo
```

## 📊 JSON Input Format

The algorithm expects JSON files with the following structure:

```json
{
    "keys": {
        "n": 4,        
        "k": 3        
    },
    "1": {
        "base": "10",   
        "value": "4"    
    },
    "2": {
        "base": "2",
        "value": "111"
    }
    
}
```

### Example: test1.json
```json
{
    "keys": {
        "n": 4,
        "k": 3
    },
    "1": {
        "base": "10",
        "value": "4"
    },
    "2": {
        "base": "2",
        "value": "111"
    },
    "3": {
        "base": "10",
        "value": "12"
    },
    "6": {
        "base": "4",
        "value": "213"
    }
}
```

## 🔍 How It Works

### 1. JSON Parsing
- Extracts `n` (number of roots) and `k` (minimum required)
- Calculates polynomial degree: `m = k - 1`
- Parses each root as `(x, y)` coordinates

### 2. Base Decoding
- Converts encoded values from various bases to decimal
- Example: `"111"` in base `2` = `7` in decimal
- Uses `BigInteger(value, base)` for conversion

### 3. Lagrange Interpolation
Uses the formula:
```
f(x) = Σ(yᵢ × Lᵢ(x))
```

Where `Lᵢ(x)` is the Lagrange basis polynomial:
```
Lᵢ(x) = Π((x - xⱼ) / (xᵢ - xⱼ)) for j ≠ i
```

### 4. Secret Extraction
- Evaluates the interpolated polynomial at `x = 0`
- The result is the constant term `c` (the secret)

## 📈 Test Results

### Test Case 1 (test1.json)
- **n = 4, k = 3, m = 2** (degree 2 polynomial)
- **Roots:**
  - (1, 4): "4" in base 10 = 4
  - (2, 7): "111" in base 2 = 7  
  - (3, 12): "12" in base 10 = 12
  - (6, 39): "213" in base 4 = 39
- **Secret (constant term): 8**

### Test Case 2 (test2.json)
- **n = 10, k = 7, m = 6** (degree 6 polynomial)
- **Roots:** 10 polynomial roots with large values
- **Secret (constant term): 79836264049581**

## 🔧 Implementation Details

### Key Classes

#### `ShamirAlgo`
Main algorithm class containing:
- `PolynomialRoot`: Holds (x, y) coordinates and encoding info
- `TestCase`: Contains test case configuration (n, k, m, roots)
- `parseJsonInput()`: Reads and parses JSON files
- `decodeFromBase()`: Converts values from different bases
- `findConstantTerm()`: Performs Lagrange interpolation

#### `Main`
Demonstration class that:
- Processes both test cases
- Displays detailed results
- Shows polynomial roots and secrets

### Mathematical Approach

1. **Polynomial Representation**: `f(x) = aₘxᵐ + aₘ₋₁xᵐ⁻¹ + ... + a₁x + c`
2. **Lagrange Interpolation**: Finds polynomial through given points
3. **Constant Term**: `c = f(0)` where `f(x)` is the interpolated polynomial

## 📚 Libraries Used

### Java Standard Libraries Only:
- **`java.io.*`**: File I/O operations
- **`java.math.BigInteger`**: Large number arithmetic
- **`java.util.*`**: Collections (ArrayList, HashMap)
- **`java.util.regex.Pattern`**: JSON parsing with regex

### No External Dependencies:
- ❌ Jackson
- ❌ Gson
- ❌ JSON.org
- ❌ Apache Commons

## 🛡️ Error Handling

The implementation includes comprehensive error handling for:
- **File I/O errors**: Missing or unreadable JSON files
- **JSON parsing errors**: Invalid JSON format
- **Base conversion errors**: Invalid base or value formats
- **Mathematical errors**: Division by zero, insufficient roots
- **Number format errors**: Invalid numeric values

## 🧮 Mathematical Background

### Shamir's Secret Sharing
Based on polynomial interpolation over a finite field:
1. **Secret Splitting**: Choose a polynomial where `f(0) = secret`
2. **Share Generation**: Create shares as points `(x, f(x))`
3. **Secret Reconstruction**: Use Lagrange interpolation to find `f(0)`

### Lagrange Interpolation
For points `(x₁, y₁), (x₂, y₂), ..., (xₙ, yₙ)`:
```
f(x) = Σ(yᵢ × Lᵢ(x))
```

Where:
```
Lᵢ(x) = Π((x - xⱼ) / (xᵢ - xⱼ)) for j ≠ i
```

## 🚀 Running Examples

```bash
# Compile the project
javac -d . src/Shamir/*.java src/Main.java

# Run the demonstration
java Main

# Run the algorithm directly
java Shamir.ShamirAlgo
```

## 📋 Requirements

- **Java 8 or higher**
- **No external dependencies**
- **JSON files in the specified format**

## 🔍 Verification

You can manually verify the results:
1. Decode the Y values from their respective bases
2. Use Lagrange interpolation to find the polynomial
3. Evaluate the polynomial at x = 0 to get the secret

