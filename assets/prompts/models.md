# AI Asset Generation Prompts for Starfall Shooter

## Overview
All 3D models and textures are AI-generated. This document contains the prompts used for consistency.

## Tools
- **3D Models**: Meshy, Tripo, CSM, or Blender + Stable Diffusion
- **Textures**: Midjourney, Stable Diffusion, Adobe Firefly
- **Particles**: Custom particle editor or AI-generated flipbook textures

## Naming Convention
- Models: `{category}_{name}.g3db` (e.g., `ship_interceptor.g3db`)
- Textures: `{category}_{name}_{type}.png` (e.g., `ship_interceptor_diffuse.png`)
- Particles: `particles/{effect}.p`

---

## Player Ships

### 1. Interceptor (Fast, Piercing)
**Style**: Sleek, angular, needle-nose fighter with glowing engine trails
**Color Scheme**: Cyan/electric blue accents on dark gunmetal
**Silhouette**: Long and narrow, swept wings

**Prompt (Meshy/Tripo)**:
```
Low-poly 3D model of a futuristic space interceptor fighter ship, 
sleek needle-nose design, swept-back angular wings, 
glowing cyan engine exhaust ports, dark gunmetal hull with electric blue trim, 
sharp geometric edges, aerodynamic, game-ready, 2000 tris max, 
PBR materials, transparent canopy, symmetrical, top-down view optimized
```

**Variants needed**:
- `ship_interceptor.g3db` (main model)
- `ship_interceptor_diffuse.png` (albedo)
- `ship_interceptor_normal.png` (normal map)
- `ship_interceptor_emissive.png` (engine glow)

### 2. Balanced (Homing)
**Style**: Classic fighter silhouette, rounded canopy, modular hardpoints
**Color Scheme**: Emerald green accents on charcoal
**Silhouette**: Balanced proportions, recognizable fighter shape

**Prompt**:
```
Low-poly 3D model of a balanced multi-role space fighter, 
classic fighter jet silhouette in space, rounded bubble canopy, 
modular weapon hardpoints on wings, glowing green engine trails, 
charcoal hull with emerald green accents, clean panel lines, 
game-ready, 2500 tris max, PBR materials, symmetrical, 
top-down camera optimized, visible weapon mounts
```

### 3. Heavy (Wide Spread, Nova)
**Style**: Broad, imposing, heavy armor plating, multiple gun barrels
**Color Scheme**: Orange/amber accents on dark bronze
**Silhouette**: Wide and chunky, heavy bomber vibe

**Prompt**:
```
Low-poly 3D model of a heavy assault space bomber, 
broad imposing hull, thick armor plating, multiple forward-facing gun barrels, 
twin massive engine nacelles with orange/amber glow, 
dark bronze hull with burnt orange trim, industrial panel lines, 
heavy shielding visible, game-ready, 3000 tris max, 
PBR materials, top-down optimized, visible weapon arrays
```

---

## Enemies

### 1. Straight Shooter (Basic)
**Style**: Small drone, simple geometry, single eye/sensor
**Color Scheme**: Red accents on dark grey
**Count**: ~500 tris

**Prompt**:
```
Low-poly 3D model of a small enemy drone, simple geometric shape, 
single glowing red sensor eye, compact triangular body, 
small thruster ports, dark grey hull with red warning lights, 
minimal detail, game-ready, 500 tris max, PBR, 
top-down optimized, expendable look
```

### 2. Spread Shooter (Advanced)
**Style**: Angular, three-pronged design, multiple gun ports
**Color Scheme**: Purple/magenta on black
**Count**: ~800 tris

**Prompt**:
```
Low-poly 3D model of an advanced enemy fighter, 
three-pronged angular design, three visible gun ports at tips, 
glowing purple energy cores, black hull with magenta circuit lines, 
sharp aggressive geometry, game-ready, 800 tris max, 
PBR materials, top-down view, elite enemy appearance
```

---

## Bosses

### Stage 1 Boss: Core Guardian
**Style**: Massive stationary core with rotating shield segments, central eye
**Color Scheme**: Gold/white on deep blue
**Count**: ~5000 tris (can be higher for boss)

**Prompt**:
```
Low-poly 3D model of a massive stage boss - Core Guardian, 
huge central spherical core with giant glowing eye, 
3 rotating shield segments/rings around core, 
each segment has weapon ports, gold and white armor on deep blue hull, 
majestic imposing scale, energy field effects, 
game-ready, 5000 tris max, PBR, top-down camera, 
clear weak points (core eye, shield generators)
```

### Stage 2 Boss: Dreadnought
**Style**: Long capital ship, multiple turrets, bridge structure
**Color Scheme**: Silver/red on dark grey
**Count**: ~6000 tris

**Prompt**:
```
Low-poly 3D model of a dreadnought boss ship, 
long capital ship silhouette, multiple rotating turret emplacements, 
prominent bridge superstructure, massive engine array, 
silver hull with red battle markings, heavy armor plating, 
game-ready, 6000 tris max, PBR, top-down, 
distinct destructible parts (turrets, engines, bridge)
```

### Stage 3 Boss: Void Entity
**Style**: Organic/mechanical hybrid, shifting geometry, particle trails
**Color Scheme**: Void black with neon pink/cyan highlights
**Count**: ~7000 tris

**Prompt**:
```
Low-poly 3D model of a final boss - Void Entity, 
organic-mechanical hybrid horror, shifting asymmetrical geometry, 
multiple articulated limbs/tentacles with weapon tips, 
void black material with neon pink and cyan energy veins, 
particle trail emitters on limbs, eldritch appearance, 
game-ready, 7000 tris max, PBR with emissive, 
top-down, multiple phases visible in model
```

---

## Projectiles

### Player Projectile
**Style**: Elongated energy bolt, glowing trail
**Color**: Cyan/blue/white gradient

**Prompt**:
```
Low-poly energy bolt projectile, elongated diamond shape, 
glowing cyan core with white-hot center, 
trail-ready geometry, 100 tris, emissive material
```

### Enemy Projectile
**Style**: Sharp red/orange plasma bolt
**Color**: Red/orange gradient

**Prompt**:
```
Low-poly enemy plasma bolt, sharp angular shape, 
glowing red-orange, menacing appearance, 100 tris, emissive
```

---

## Pickups

### Weapon Upgrade
**Icon**: Upward arrow / plus symbol
**Color**: Green glow

### Ultimate Charge
**Icon**: Lightning bolt / star
**Color**: Gold/yellow glow

### Health
**Icon**: Heart / cross
**Color**: Red glow

### Score Bonus
**Icon**: Star / coin
**Color**: White/blue glow

**Prompt (all pickups)**:
```
Low-poly pickup item, floating geometric shape (cube/octahedron), 
glowing [color] core, rotating animation ready, 
distinct silhouette for each type, 200 tris, 
emissive material, top-down visible
```

---

## Particle Effects

### Explosion
- Expanding ring + debris + smoke
- Flipbook texture: 16 frames, 512x512

### Hit Spark
- Small radial burst
- Flipbook: 8 frames, 128x128

### Engine Trail
- Stretched particles, additive blending
- Color gradient per ship type

### Ultimate Effects
- Interceptor: Rapid muzzle flashes + speed lines
- Heavy: Expanding nova ring + shockwave
- Balanced: Homing missile trails + target indicators

---

## Export Pipeline

1. **Generate** in Meshy/Tripo/CSM
2. **Import** to Blender
3. **Clean up**: Decimate to target tris, fix normals, UV unwrap
4. **Texture** in Substance Painter or Blender (PBR: albedo, normal, roughness, metalness, emissive)
5. **Export** as `.glb` or `.gltf`
6. **Convert** using LibGDX `fbx-conv`:
   ```bash
   fbx-conv -f model.glb model.g3db
   ```
6. **Place** in `android/assets/models/`

---

## Texture Specifications
- **Resolution**: 512x512 (ships), 256x256 (enemies/projectiles), 1024x1024 (bosses)
- **Format**: PNG (lossless) → compressed to ETC2/ASTC at build
- **Channels**: RGBA (albedo + alpha for emissive mask)
- **Naming**: `{model}_{map}.png`

---

## Placeholder Strategy
Until AI assets are ready, use LibGDX primitives:
- `ModelBuilder` for boxes/spheres/cylinders
- Color-coded by type (cyan=player, red=enemy, gold=boss)
- `ModelBuilder` code in `ModelGenerator.kt` (see below)