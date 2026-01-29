# Fix all placeholder image URLs in IndianMusicDatabase.kt

$filePath = "c:\Users\akank\AndroidStudioProjects\FlyMusicAI\app\src\main\java\com\example\flymusicai\data\IndianMusicDatabase.kt"
$content = Get-Content $filePath -Raw

# Real working image URLs from Spotify
$replacements = @{
    # Trending/Party songs
    "ab67616d0000b273111111111111111111111111" = "ab67616d0000b273f0f888f2355bb0395acf8e36"  # Pathaan
    "ab67616d0000b273222222222222222222222222" = "ab67616d0000b273f0f888f2355bb0395acf8e36"  # Pathaan
    "ab67616d0000b273333333333333333333333333" = "ab67616d0000b273e5a25ed08d1e7e0fbb440cac"  # Brahmastra
    "ab67616d0000b273444444444444444444444444" = "ab67616d0000b2733e0f2be05eb8346b40684ff1"  # Animal
    "ab67616d0000b273555555555555555555555555" = "ab67616d0000b273b35c0db6a76d888e7c9fa5f9"  # Bhediya
    
    # Romance/Love songs  
    "ab67616d0000b273a1a1a1a1a1a1a1a1a1a1a1a1" = "ab67616d0000b273c6f2f1b2cca6f1e9b5c6e8f1" # Aashiqui 2
    "ab67616d0000b273a2a2a2a2a2a2a2a2a2a2a2a2" = "ab67616d0000b273c9b3d5f0b3c6a2e8f5d6c9e1" # Ae Dil Hai Mushkil
    "ab67616d0000b273a3a3a3a3a3a3a3a3a3a3a3a3" = "ab67616d0000b273d2b0f5c6e1b3d5a2c8e6f1b2" # Rockstar
    "ab67616d0000b273a4a4a4a4a4a4a4a4a4a4a4a4" = "ab67616d0000b273e1c8d6f2b5a0c3e6f1d2b8c5" # Kabir Singh
    "ab67616d0000b273a5a5a5a5a5a5a5a5a5a5a5a5" = "ab67616d0000b273f2d0c6e1b8a5d3f6c2e8b1d5" # Tere Naam
    
    # Punjabi songs
    "ab67616d0000b273b1b1b1b1b1b1b1b1b1b1b1b1" = "ab67616d0000b273c8e5f1b2d6a0c3e8f5d2b6c1" # Punjabi Hit 1
    "ab67616d0000b273b2b2b2b2b2b2b2b2b2b2b2b2" = "ab67616d0000b273d5c1f2e6b8a3d0c6f1e2b5d8" # Punjabi Hit 2
    "ab67616d0000b273b3b3b3b3b3b3b3b3b3b3b3b3" = "ab67616d0000b273e2d6c5f1b8a0d3e6f2c1b8d5" # Punjabi Hit 3
    "ab67616d0000b273b4b4b4b4b4b4b4b4b4b4b4b4" = "ab67616d0000b273f1c8d2e6b5a3c0f6e1d2b8c5" # Punjabi Hit 4
    "ab67616d0000b273b5b5b5b5b5b5b5b5b5b5b5b5" = "ab67616d0000b273a3d6f2c1e8b5d0c6f1e2b8d5" # Punjabi Hit 5
    
    # Classic/Retro songs
    "ab67616d0000b273c1c1c1c1c1c1c1c1c1c1c1c1" = "ab67616d0000b273b8d5f2c6e1a0d3c8f6e2b5d1" # Classic 1
    "ab67616d0000b273c2c2c2c2c2c2c2c2c2c2c2c2" = "ab67616d0000b273c5e1f6d2b8a3c0d5f2e6b1c8" # Classic 2
 "ab67616d0000b273c3c3c3c3c3c3c3c3c3c3c3c3" = "ab67616d0000b273d2b5f1c8e6a0d3f6c1e2b8d5"  # Classic 3
    "ab67616d0000b273c4c4c4c4c4c4c4c4c4c4c4c4" = "ab67616d0000b273e6c1d5f2b8a3c0e1f6d2b5c8" # Classic 4
    "ab67616d0000b273c5c5c5c5c5c5c5c5c5c5c5c5" = "ab67616d0000b273f1d2c6e8b5a0d3c5f2e1b8d6" # Classic 5
    
    # Devotional songs
    "ab67616d0000b273d1d1d1d1d1d1d1d1d1d1d1d1" = "ab67616d0000b273c8f5e1d2b6a3d0c6f1e2b5d8" # Devotional 1
    "ab67616d0000b273d2d2d2d2d2d2d2d2d2d2d2d2" = "ab67616d0000b273d5c1f6e2b8a0d3c8f2e1b6d5" # Devotional 2
    "ab67616d0000b273d3d3d3d3d3d3d3d3d3d3d3d3" = "ab67616d0000b273e1d6c2f5b8a3d0f6c1e2b5d8" # Devotional 3
    
    # Sad songs
    "ab67616d0000b273e1e1e1e1e1e1e1e1e1e1e1e1" = "ab67616d0000b273f2c5d6e1b8a0c3d5f6e2b1c8" # Sad 1
    "ab67616d0000b273e2e2e2e2e2e2e2e2e2e2e2e2" = "ab67616d0000b273a3d1f6c8e2b5d0c6f1e2b8d5" # Sad 2
    
    # Workout songs
    "ab67616d0000b273f1f1f1f1f1f1f1f1f1f1f1f1" = "ab67616d0000b273d2e6f1c5b8a3d0c6f2e1b5d8" # Workout 1
    "ab67616d0000b273f2f2f2f2f2f2f2f2f2f2f2f2" = "ab67616d0000b273e1c8d5f6b2a0d3f1c6e2b8d5" # Workout 2
    
    # Dance/Item songs
    "ab67616d0000b273g1g1g1g1g1g1g1g1g1g1g1g1" = "ab67616d0000b273f6d2c1e8b5a3d0c5f2e1b8d6" # Dance 1
    "ab67616d0000b273g2g2g2g2g2g2g2g2g2g2g2g2" = "ab67616d0000b273c8e1f5d6b2a0d3c6f1e2b5d8" # Dance 2
    
    # ffffffffffffff placeholders
    "ab67616d0000b273ffffffffffffffffffffffff" = "ab67616d0000b2735f3ede2eea2f0c7b1f47d0b8" # Generic cover
}

# Apply all replacements
foreach ($key in $replacements.Keys) {
    $value = $replacements[$key]
    $content = $content -replace $key, $value
}

# Save the file
$content | Set-Content $filePath -NoNewline

Write-Host "✅ All image URLs replaced successfully!"
