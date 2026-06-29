# Crossbow Arsenal

Crossbow Arsenal 是一个面向 **Minecraft 1.21.1 Fabric** 的原版弩玩法增强模组。它围绕原版弩加入连射、锁敌瞄准、箭矢追踪、方块与生物穿透、爆炸附魔以及特殊箭矢，并提供可配置的 HUD 和强度选项。

本模组不会替换原版弩，而是在原有装填、附魔和弹药机制上扩展玩法。

## 模组信息 | Information

| 项目 | 内容 |
| --- | --- |
| 模组名称 | Crossbow Arsenal |
| Mod ID | `crossbow_arsenal` |
| Minecraft | `1.21.1` |
| 加载器 | Fabric |
| Java | 21 或更高版本 |
| 作者 | shouyun |
| 协议 | MIT License |

## 前置依赖 | Dependencies

| 依赖 | 用途 |
| --- | --- |
| Fabric API | Fabric 基础 API，必需 |
| Cloth Config API | 配置界面支持，必需 |
| Cardinal Components API | 保存和同步锁敌状态，必需 |
| Mod Menu | 在游戏内打开配置界面，建议安装 |

多人游戏中，服务端和客户端应使用相同版本的 Crossbow Arsenal 及对应前置依赖。

## 主要功能 | Features

### 连弩 Repeating

- 新增最高 III 级的“连弩（Repeating）”弩附魔。
- 按住使用键时，弩可连续发射多支箭。
- 可配置各等级连射数量、射击间隔、伤害倍率和散布倍率。
- 连射路径与普通弩共用弹药、追踪、穿透及爆炸处理。
- 已处理连续射击与原版右键装填之间的冲突。

### 锁敌瞄准器 Lock-on Sight

- 新增物品“锁敌瞄准器（Lock-on Sight）”。
- 锁敌瞄准器可由望远镜、紫水晶碎片、红石和铜锭合成。
- 将锁敌瞄准器与原版弩放入合成栏，可把瞄准器安装到该弩上。
- 手持已安装瞄准器的弩时，会显示科技风格启动 HUD 和锁敌界面。
- 可在屏幕范围内选择目标，并由服务端校验锁敌结果。
- 支持 Boss、亡灵生物、敌对生物和其他生物的目标优先级。

### 追踪箭 Homing Arrows

- 安装瞄准器并锁定目标后，射出的箭会持续修正飞行方向。
- 支持移动目标预测、重力补偿、末端磁吸和命中辅助。
- 连弩射出的每支箭都可以独立追踪。
- 普通箭、穿透箭和高爆箭都可以使用锁敌追踪。
- 可配置追踪距离、持续时间、不同目标类型的追踪强度和 HUD 显示。

### 穿透系统 Penetration

- 箭矢可按规则穿透玻璃、树叶、蜘蛛网等脆弱方块。
- 支持连续命中多个生物，并在每次穿透后降低伤害。
- 与原版 Piercing（穿透）附魔等级联动。
- 穿透箭可进一步穿透泥土、沙子、木材等配置允许的方块。
- 普通模式下不能穿透石头、深板岩等硬方块。
- 超载模式下，穿透箭可按配置破坏并穿过更多木质和石质方块；每次硬方块穿透都会降低速度和伤害，并受到最大穿透数量限制。
- 基岩、黑曜石、强化深板岩、命令方块、容器、机器方块及其他重要 BlockEntity 不会被破坏。

### 特殊箭矢 Special Arrows

#### 穿透箭 Penetrating Arrow

- 面向穿透玩法的专用箭矢。
- 默认可穿透玻璃、脆弱方块，并可配合 Piercing 解锁更强的软方块和木质方块穿透。
- 可与锁敌追踪及爆炸附魔共同工作。

#### 高爆箭 Explosive Arrow

- 命中方块或生物后产生爆炸。
- 可配置爆炸威力、击退、自伤倍率、是否产生火焰以及是否破坏方块。
- 可与锁敌追踪和 Explosive 附魔共同工作，最终威力受配置上限控制。

### 爆炸附魔 Explosive

- 新增最高 III 级的“爆炸（Explosive）”弩附魔。
- 等级越高，箭矢命中后的爆炸威力越高。
- 普通箭、穿透箭和高爆箭都能受到该附魔影响。
- 可通过附魔台获得，也可通过附魔书和铁砧合并。

### 超载锁敌模式 Overpowered Targeting

> [!WARNING]
> 超载锁敌模式会严重破坏游戏平衡。该模式只建议用于单人娱乐，或在明确允许此功能的服务器中使用。请勿在未获得许可的多人服务器中开启。

- 默认关闭，必须由玩家在配置界面主动启用。
- 可分别允许锁定玩家、锁定隐身目标、隔墙锁定和隔墙追踪。
- 可提高超载模式下的最大锁敌距离。
- 可允许穿透箭破坏更多木质和石质方块，并配置穿透上限、速度倍率和伤害倍率。
- 客户端和服务端的限制会共同决定实际生效的超载能力。

## 附魔兼容 | Enchantment Compatibility

| 组合 | 兼容情况 |
| --- | --- |
| Repeating + Piercing | 可以共存 |
| Explosive + Piercing | 可以共存 |
| Repeating + Explosive + Piercing | 可以共存 |
| Quick Charge | 按原版规则正常兼容 |
| Unbreaking | 正常兼容 |
| Mending | 正常兼容 |
| Multishot + Piercing | 保留原版互斥规则 |
| Repeating + Multishot | 不兼容 |

Repeating 和 Explosive 均可通过附魔台获得。原版附魔的获取方式和默认规则不会被替换。

## 配置 | Configuration

安装 Mod Menu 后，可在模组列表中选择 Crossbow Arsenal 并打开配置界面。高级用户也可以编辑游戏目录下的 `config/crossbow_arsenal.json`；修改配置前建议先备份文件。

主要配置分类包括：

- 连弩：启用开关、各等级射击数量、射击间隔、伤害和散布倍率。
- 锁敌瞄准：锁敌距离、屏幕边距、服务端校验范围和目标选择规则。
- 追踪：追踪持续时间、追踪强度、末端磁吸、命中辅助和连弩追踪倍率。
- 穿透：玻璃、脆弱方块、生物、软方块和木质方块穿透规则。
- 特殊箭与爆炸：特殊箭开关、爆炸威力、击退、自伤、火焰和地形破坏。
- 超载模式：玩家/隐身/隔墙锁定、隔墙追踪、硬方块破坏和最大穿透数量。
- HUD：启动 HUD、锁敌 HUD、透明度、持续时间、提示音和调试显示。

多人服务器应由服主统一决定超载锁敌、爆炸地形破坏等高影响配置。

## 安装方法 | Installation

1. 安装适用于 Minecraft 1.21.1 的 Fabric Loader。
2. 安装 Fabric API。
3. 安装 Cloth Config API。
4. 安装 Cardinal Components API。
5. 建议安装 Mod Menu，以便在游戏内管理配置。
6. 将 Crossbow Arsenal 的 `.jar` 文件放入游戏目录的 `mods` 文件夹。
7. 启动游戏，并确认主菜单的模组列表中出现 Crossbow Arsenal。

服务端安装时，也需要把模组及服务端所需前置放入服务器的 `mods` 文件夹。

## 注意事项 | Notes

- 超载锁敌模式默认关闭。
- 高爆箭和 Explosive 附魔会明显提高输出能力，请根据游戏环境调整配置。
- 开启爆炸方块破坏或超载硬方块穿透后，箭矢可能改变地形。
- 方块穿透和爆炸判定由服务端执行，客户端主要负责 HUD、粒子和声音显示。
- 在整合包或多人服务器中使用前，建议先备份世界并完成兼容性测试。

## 从源码构建 | Build from Source

需要 Java 21。克隆仓库后，在项目目录运行：

```powershell
.\gradlew.bat build
```

构建产物位于 `build/libs/`。

## 作者与联系 | Author & Contact

- 作者：shouyun
- 抖音：[shouyun](https://www.douyin.com/user/self?from_tab_name=main)
- bilibili：[shouyun](https://space.bilibili.com/1832031043?)
- QQ：2491242401

## 协议 | License

This project is licensed under the [MIT License](LICENSE).
