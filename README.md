# Armor Template JSON 临时使用说明

这是一个 Minecraft Java 版 NeoForge `1.21.1` 模组。当前阶段实现了 JSON 盔甲模板、异常显示、本地化名称、插板定义、插板 UI、盔甲物品生成、预定义套装效果，以及 Java 侧效果注册接口。

## 安装

1. 使用 Minecraft `1.21.1` 和 NeoForge。
2. 将构建出的 `armortemplatejson-0.1.0.jar` 放入客户端或服务端 `mods` 文件夹。
3. 启动游戏或服务端后，模组列表中应出现 `Armor Template JSON`。

## 数据包路径

盔甲模板：

```text
data/<namespace>/armortemplatejson/armor_templates/<template_id>.json
```

插板定义：

```text
data/<namespace>/armortemplatejson/plugins/<plugin_id>.json
```

## name 本地化写法

简单字符串会被当作翻译键：

```json
"name": "item.example.bronze_chestplate"
```

对象写法支持翻译键和 fallback：

```json
"name": {
  "translate": "item.example.bronze_chestplate",
  "fallback": "Bronze Chestplate"
}
```

调试时也可以使用字面量：

```json
"name": {
  "literal": "测试胸甲"
}
```

## 盔甲模板示例

```json
{
  "properties": {
    "level": 1,
    "icon": "minecraft:iron_chestplate",
    "name": {
      "translate": "item.example.bronze_chestplate",
      "fallback": "Bronze Chestplate"
    },
    "slot": "body",
    "plugin_numbers": 3,
    "effect": [
      "armortemplatejson:elytra_flight",
      "armortemplatejson:damage_reflection"
    ],
    "properties": [
      {
        "type": "minecraft:generic.armor",
        "operation": "add",
        "value": 6.0
      }
    ]
  }
}
```

`slot` 支持 `head`、`body`、`legging`、`feet`，也兼容 `helmet`、`chestplate`、`leggings`、`boots`。

`plugin_numbers` 范围是 `0` 到 `6`。大于 `0` 时，生成盔甲栈会带插板槽，潜行右键可打开 UI。

## 预定义效果

效果写在盔甲模板 `properties.effect` 字段中。可以写单个 ID，也可以写数组。

```json
"effect": "armortemplatejson:piglin_neutral"
```

当前预定义效果 ID：

- `armortemplatejson:elytra_flight`：胸甲在四件套激活时可作为鞘翅飞行。
- `armortemplatejson:creative_flight`：四件套激活时授予 NeoForge `creative_flight` 属性。
- `armortemplatejson:damage_reflection`：四件套激活时按实际受到的最终伤害反弹给攻击者。
- `armortemplatejson:powder_snow_walking`：四件套激活时获得细雪行走能力。
- `armortemplatejson:piglin_neutral`：四件套激活时让猪灵保持中立。

套装效果激活规则：玩家或实体必须同时穿戴头盔、胸甲、护腿、靴子四个由模板生成的盔甲，并且四件的 `level` 必须相同。不同模板但同等级的四件盔甲可以组成套装；激活后会合并四件盔甲上声明的效果 ID。

## Java 效果注册接口

外部 Java 代码可以注册自己的效果实现：

```java
ArmorEffectRegistry.register(
    ArmorTemplateJsonMod.id("my_effect"),
    new ArmorTemplateEffect() {
        // 按需覆盖 canElytraFly、onPlayerTick、onLivingDamagePost 等方法。
    }
);
```

JSON 中引用该 ID 即可使用：

```json
"effect": "armortemplatejson:my_effect"
```

当前只提供 Java 侧注册接口。自定义 `.class` 字节码加载、消耗物品、插板效果组合和正式文档体系仍未接入。

## 插板 UI

1. 主手拿一个由模板生成、且 `plugin_numbers > 0` 的盔甲栈。
2. 潜行右键打开插板界面。
3. 上方是插板槽，下方是玩家背包。
4. 打开界面的热键栏槽会被锁定，防止编辑中的盔甲被移动。
5. 关闭界面时，服务端会把插板槽内容写回盔甲组件。

## 代码生成入口

盔甲栈：

```java
ItemStack armor = ArmorTemplateStackFactory.createStack(templateId, armorTemplate);
```

插板栈：

```java
ItemStack plugin = PluginStackFactory.createStack(pluginId, pluginDefinition);
```

## IDLE 打开源码

桌面上的 `OpenArmorTemplateJsonSourcesInIDLE.bat` 可用于用 IDLE 打开核心源码文件。项目内也保留脚本：

```text
tools/open_sources_in_idle.py
```

## 构建

项目目录：

```text
C:\Users\35753\Documents\Codex\2026-06-16\files-mentioned-by-the-user-docx\work\armor-template-json
```

构建命令：

```powershell
gradlew.bat build
```
