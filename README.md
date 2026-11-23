# GameInfo Unofficial Port (中文)

GameInfo Unofficial Port 是 ifcifc 原版 [GameInfo](https://www.curseforge.com/minecraft/mc-mods/gameinfo) 的非官方移植版。本模组针对高版本 Minecraft 进行了代码适配、重构与功能扩展，由社区维护，并非官方版本。

> **版权与来源声明**
> 本项目基于 basketikun、ifcifc 的 GameInfo 早期版本开发，原始代码版权归原作者所有。本项目对原始代码的修改部分依据 **GNU LGPL-3.0** 许可证开源。如您是原作者且不希望此项目继续分发，请通过 GitHub 联系我，我将立即删除相关代码及发布物。

模组提供 FPS、坐标显示、准星方块与实体检测、熔炉数据同步、史莱姆区块判定、可滚动配置界面以及客户端与服务端之间的网络通信。**客户端必须安装**；服务端可选安装，安装后部分功能才完整。

**快照版本下载：[GitHub Releases](https://github.com/CodaR5/gameinfo/releases)**

## 功能特性

HUD 显示项均可单独开关：

- **FPS**：实时帧率显示。
- **时间**：游戏天数与游戏内时间。
- **坐标与朝向**：当前精确坐标及八方位朝向。
- **生物群系**：当前所在的原版群系名称。
- **下界坐标转换**：主世界显示 `N-XZ`（坐标 / 8），下界显示 `O-XZ`（坐标 × 8）。
- **装备耐久**：屏幕右侧显示穿戴中且有耐久的装备。
- **版本号**：右上角显示版本，可自定义，也可跟随游戏版本。
- **方块信息**：准星指向的方块名称、坐标与挖掘进度。
- **实体血量**：准星指向的实体名称与精确血量（若数值大于 1000 则显示 `NAN`）。
- **熔炉信息**：准星指向熔炉或打开熔炉界面时，显示输入、燃料、输出槽的物品。
- **史莱姆区块**：坐标行末尾追加绿色 `- Slime Chunk` 标记。
- **种子设定**：纯净服种子设定，若服务端已安装本模组则自动获取，无需手动干预。

**按键与命令：**

- `U`：打开配置界面。
- `/gameinfo`：模组主命令。
- `/chunk`：区块管理命令，支持 `load`、`unload`、`list`、`check`。
- `/giseed`：管理你的游戏种子，支持 `show`、`set`、`clear`。

配置界面为单页滚动布局，分为基础信息、高级信息、自定义位置与颜色三组。安装 Mod Menu 后可直接从 Mod Menu 打开。

## 安装指南

- **客户端安装**：将对应版本的 `.jar` 放入 `.minecraft/mods/` 后启动游戏。
- **服务端安装（可选但建议）**：安装后，熔炉数据由服务端实时扫描并同步，避免客户端数据延迟；多人游戏中的史莱姆区块由服务端计算并下发结果，**不向客户端暴露世界种子**。若服务端未安装，客户端不会崩溃，史莱姆区块在多人模式下默认不显示，熔炉数据将使用缓存。单机模式下史莱姆区块由本地计算，不依赖服务端。

## 配置说明

配置文件位于 `config/gameinfo.properties`，包含全局开关、颜色、位置以及各功能独立开关。游戏内按 `U` 也可直接修改，修改后即时生效。

## 多语言支持

内置 2 种语言：英语（美国）、简体中文。

---

# GameInfo Unofficial Port (English)

GameInfo Unofficial Port is an unofficial port of ifcifc's original [GameInfo](https://www.curseforge.com/minecraft/mc-mods/gameinfo), with code adaptation, refactoring, and feature expansion for newer Minecraft versions. It is maintained by the community and is not an official version.

> **Copyright and Source Notice**
> This project is developed based on an early version of GameInfo by basketikun and ifcifc. The copyright of the original code belongs to the original authors. **The modifications to the original code in this project are open-sourced under the GNU LGPL-3.0 license.** If you are the original author and do not wish this project to continue being distributed, please contact me via GitHub and I will immediately delete the relevant code and releases.

The mod provides FPS display, coordinate display, crosshair block and entity detection, furnace data synchronization, slime chunk detection, a scrollable configuration screen, and network communication between client and server. **The client must have it installed**; server installation is optional, and some features are only complete when it is installed.

**Snapshot version download: [GitHub Releases](https://github.com/CodaR5/gameinfo/releases)**

## Features

All HUD display items can be toggled individually:

- **FPS**: Real-time frames per second.
- **Time**: Game days and in-game time.
- **Coordinates & Facing**: Current exact coordinates and 8-direction facing.
- **Biome**: Name of the current vanilla biome.
- **Nether Coordinate Conversion**: The Overworld displays `N-XZ` (coordinates / 8), and the Nether displays `O-XZ` (coordinates × 8).
- **Equipment Durability**: Displays worn equipment with durability on the right side of the screen.
- **Version Number**: Displayed in the top-right corner, customizable, or can follow the game version.
- **Block Info**: Name, coordinates, and breaking progress of the block under the crosshair.
- **Entity Health**: Name and exact health of the entity under the crosshair (displays `NAN` if greater than 1000).
- **Furnace Info**: When the crosshair points at a furnace or when a furnace is open, displays items in the input, fuel, and output slots.
- **Slime Chunk**: Appends a green `- Slime Chunk` marker after the coordinate line.
- **Seed Configuration**: Vanilla server seed configuration. If the server has the mod installed, it is fetched automatically.

**Keys and Commands:**

- `U`: Open the configuration screen.
- `/gameinfo`: Main mod command.
- `/chunk`: Chunk management command, supporting `load`, `unload`, `list`, and `check`.
- `/giseed`: Manage your game seed, supporting `show`, `set`, and `clear`.

The configuration screen uses a single-page scrolling layout, divided into three groups: Basic Info, Advanced Info, and Custom Position & Color. If Mod Menu is installed, it can be opened directly from Mod Menu.

## Installation

- **Client Installation**: Place the corresponding version's `.jar` into `.minecraft/mods/` and launch the game.
- **Server Installation (Optional but Recommended)**: Once installed, furnace data is scanned and synchronized in real-time by the server, avoiding client-side data delay. In multiplayer, slime chunks are calculated by the server and the result is sent to the client, **without exposing the world seed to the client**. If the server does not have this mod installed, the client will not crash; slime chunks will not be displayed in multiplayer, and furnace data will use the cache. In singleplayer, slime chunks are calculated locally and do not depend on the server.

## Configuration

The configuration file is located at `config/gameinfo.properties` and contains global switches, colors, positions, and independent switches for each feature. You can also modify it directly in-game by pressing `U`, and changes take effect immediately.

## Multi-language Support

Built-in 2 languages: English (US), Simplified Chinese.
