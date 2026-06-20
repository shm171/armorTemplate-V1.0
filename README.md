# Armor Template JSON 使用教程

这是一个 Minecraft Java 版 NeoForge `1.21.1` 模组，模组 ID 为 `armortemplatejson`。

当前实现范围：
- JSON 盔甲模板注册、异常类与数据包加载异常显示
- 所有 `name` 字段支持本地化键名
- 盔甲和插板支持 `description` 功能描述，并写入原版物品堆叠组件 `lore`
- 盔甲物品生成、插板物品生成、插板 UI
- 胸甲专用插板配置、插板类别、最高支持插板等级
- 插板自身效果、插板组合效果、预定义效果注册接口
- 消耗数据层，只保存量的类型、剩余量、容量上限、消耗速率，不执行真实扣除
- `custom_model_data` 资源包模型覆盖，并保留 `visual.texture`、`visual.model` 资源 ID
- 内置一套可进游戏测试的样例盔甲和插板

## 安装和构建

开发环境：
- Minecraft `1.21.1`
- NeoForge `21.1.200`
- Java `21`

构建命令：
```powershell
.\gradlew.bat build
```

构建出的 jar 在：
```text
build/libs/
```

把 jar 放入客户端或服务端 `mods` 文件夹即可加载。

## 进游戏查看内置样例

进入世界后执行：
```mcfunction
/armortemplatejson give_sample
```

命令会给玩家：
- `sample_helmet`
- `sample_chestplate`
- `sample_leggings`
- `sample_boots`
- `guard_plate`
- `flight_core`

主手拿样例胸甲，按住潜行并右键，可以打开插板 UI。只有 `slot` 为 `body` 且配置了插板槽的胸甲可以打开 UI，头盔、护腿、靴子都不会打开插板 UI。

## JSON 文件路径

盔甲模板：
```text
data/<namespace>/armortemplatejson/armor_templates/<template_id>.json
```

插板定义：
```text
data/<namespace>/armortemplatejson/plugins/<plugin_id>.json
```

插板组合效果：
```text
data/<namespace>/armortemplatejson/plugin_combinations/<combination_id>.json
```

内置样例位于：
```text
src/main/resources/data/armortemplatejson/armortemplatejson/
```

## 本地化名称

所有 `name` 字段都支持三种写法。

只写字符串时，它会被当成本地化键名：
```json
"name": "template.example.steel_chestplate"
```

推荐写法是对象，带 `translate` 和 `fallback`：
```json
"name": {
  "translate": "template.example.steel_chestplate",
  "fallback": "Steel Chestplate"
}
```

调试时也可以写字面量：
```json
"name": {
  "literal": "测试胸甲"
}
```

如果写了 `translate`，语言文件中找不到键名时会显示 `fallback`。

## 功能描述 Lore

盔甲模板和插板定义都支持 `description` 字段，写法和 `name` 一样。生成物品时，`description` 会写入原版物品堆叠组件 `lore`，也就是鼠标悬停物品时显示的功能描述。

示例：
```json
"description": {
  "translate": "template.example.steel_chestplate.desc",
  "fallback": "Sneak-use this chestplate to open plugin slots."
}
```

如果不写 `description`，生成物品不会额外写入 lore。

## 视觉资源和资源包

盔甲和插板都可以写 `visual`：
```json
"visual": {
  "texture": "armortemplatejson:item/sample_chestplate",
  "model": "armortemplatejson:item/sample_chestplate",
  "custom_model_data": 1002
}
```

字段说明：
- `texture`：贴图资源 ID，用于数据记录和资源包约定。
- `model`：模型资源 ID，用于数据记录和资源包约定。
- `custom_model_data`：真正写入物品堆叠的原版组件，资源包可以通过模型 override 读取它。

注意：Minecraft `1.21.1` 没有可由物品堆直接指定任意 `item_model` 的组件，所以当前运行时只会自动应用 `custom_model_data`。要让资源包生效，可以覆盖本模组的模型文件，或使用同样的 `custom_model_data` override 指向自己的模型。

资源包示例：
```json
{
  "parent": "minecraft:item/generated",
  "textures": {
    "layer0": "example:item/my_chestplate"
  }
}
```

可放在：
```text
assets/armortemplatejson/models/item/sample_chestplate.json
```

## 盔甲模板字段

完整胸甲示例：
```json
{
  "properties": {
    "level": 1,
    "icon": "minecraft:diamond_chestplate",
    "name": {
      "translate": "template.example.steel_chestplate",
      "fallback": "Steel Chestplate"
    },
    "description": {
      "translate": "template.example.steel_chestplate.desc",
      "fallback": "Sneak-use this chestplate to open plugin slots."
    },
    "visual": {
      "texture": "example:item/steel_chestplate",
      "model": "example:item/steel_chestplate",
      "custom_model_data": 1102
    },
    "slot": "body",
    "effect": [
      "armortemplatejson:elytra_flight"
    ],
    "chestplate": {
      "plugin_numbers": 2,
      "plugin_categories": [
        "example:defense",
        "example:flight"
      ],
      "max_plugin_level": 2,
      "consumption": [
        {
          "type": "mekanism:energy",
          "remaining": 10000,
          "capacity": 10000,
          "consume_rate": 20
        }
      ]
    },
    "properties": [
      {
        "type": "minecraft:generic.armor",
        "operation": "add",
        "value": 8.0
      },
      {
        "type": "minecraft:generic.armor_toughness",
        "operation": "add",
        "value": 2.0
      }
    ]
  }
}
```

字段说明：
- `level`：盔甲等级，必须大于或等于 `0`。一套盔甲四件必须等级相同，套装效果才会激活。
- `icon`：图标资源 ID，当前作为数据字段保存，后续可用于界面显示。
- `name`：本地化名称。
- `description`：功能描述文本，生成物品时写入 `lore`。
- `visual`：资源包视觉数据和 `custom_model_data`。
- `slot`：装备槽位，支持 `head`、`body`、`legging`、`feet`，也兼容 `helmet`、`chestplate`、`leggings`、`boots`。
- `effect`：盔甲自身提供的效果 ID，可以写单个 ID 或数组。
- `properties`：原版属性列表，目前只允许 `minecraft` 命名空间下确实存在的原版属性。

### 胸甲专用配置项

`chestplate` 只允许写在 `slot` 为 `body` 的盔甲模板里。头盔、护腿、靴子不能写 `chestplate`，否则数据包加载会报错。

字段说明：
- `plugin_numbers`：插板槽数量，范围 `0` 到 `6`。需要使用插板 UI 时建议写 `1` 到 `6`。
- `plugin_categories`：允许安装的插板类别。为空数组或不写时，表示不限制类别。
- `max_plugin_level`：该胸甲最高支持的插板等级。插板等级小于或等于该值才可放入。
- `consumption`：胸甲自身携带的消耗数据，只保存数据，不做真实扣除。

放入插板时不再要求“插板等级必须等于盔甲等级”，而是按 `max_plugin_level` 判断。

## 插板定义字段

示例：
```json
{
  "level": 1,
  "category": "example:defense",
  "icon": "minecraft:amethyst_shard",
  "name": {
    "translate": "plugin.example.guard_plate",
    "fallback": "Guard Plate"
  },
  "description": {
    "translate": "plugin.example.guard_plate.desc",
    "fallback": "A defensive plugin for level 1 armor."
  },
  "visual": {
    "texture": "example:item/guard_plate",
    "model": "example:item/guard_plate",
    "custom_model_data": 2101
  },
  "effects": {
    "effect": "armortemplatejson:damage_reflection",
    "per_level_effect": [],
    "unlock_effect": [
      {
        "level": 2,
        "effect": "armortemplatejson:piglin_neutral"
      }
    ]
  },
  "consumption": [
    {
      "type": "minecraft:emerald",
      "remaining": 3,
      "capacity": 3,
      "consume_rate": 0
    }
  ]
}
```

字段说明：
- `level`：插板等级，必须大于或等于 `0`。
- `category`：插板类别，用来和胸甲的 `plugin_categories` 匹配。
- `icon`：插板图标资源 ID。
- `name`：本地化名称。
- `description`：功能描述文本，生成插板物品时写入 `lore`。
- `visual`：资源包视觉数据和 `custom_model_data`。
- `effects.effect`：基础效果，可以写单个 ID 或数组。
- `effects.per_level_effect`：每级提升的效果 ID，可以写单个 ID 或数组。
- `effects.unlock_effect`：达到指定等级才解锁的效果列表。
- `consumption`：插板自身携带的消耗数据。

插板放入规则：
- 物品必须是本模组生成的插板物品。
- 只能放入胸甲的插板 UI。
- 插板 `level` 必须小于或等于胸甲 `chestplate.max_plugin_level`。
- 如果胸甲写了 `plugin_categories`，插板 `category` 必须在列表中。
- 插板槽最大堆叠数为 `1`。

## 插板组合效果

组合效果使用单独 JSON。组合匹配不要求插板顺序，胸甲插板槽内的所有有效插板会合并参与匹配。

示例：
```json
{
  "name": {
    "translate": "plugin_combination.example.guarded_flight",
    "fallback": "Guarded Flight"
  },
  "min_level": 1,
  "combinations": [
    [
      {
        "category": "example:defense",
        "min_level": 1
      },
      {
        "category": "example:flight",
        "min_level": 1
      }
    ]
  ],
  "effects": {
    "effect": "armortemplatejson:piglin_neutral",
    "per_level_effect": [],
    "unlock_effect": []
  }
}
```

字段说明：
- `name`：组合效果名称，支持本地化。
- `min_level`：组合最低触发等级。匹配到的插板中最低等级必须大于或等于该值。
- `combinations`：支持的插板组合，是“组合列表”。每个内部数组表示一种可触发组合。
- `combinations[][].category`：需要的插板类别。
- `combinations[][].min_level`：该类别插板的最低等级，不写时为 `0`。
- `effects.effect`：组合成功后的基础效果。
- `effects.per_level_effect`：组合每级提升效果。
- `effects.unlock_effect`：组合达到指定等级才解锁的效果。

匹配规则：
- `A + B` 和 `B + A` 等价。
- 可以写两块插板，也可以写更多插板，例如 `A + B + C + D`。
- 同一块插板不会重复满足同一个组合中的多个需求。
- 额外插板不会阻止组合效果触发。

## 消耗量数据

本模组当前不会真的扣除物品、液体、气体或能量。它只在盔甲和插板数据里保存：
- 量的类型
- 剩余量
- 容量上限
- 消耗速率

示例：
```json
{
  "type": "mekanism:energy",
  "remaining": 10000,
  "capacity": 10000,
  "consume_rate": 20
}
```

字段说明：
- `type`：量的类型，是任意 `ResourceLocation`。模组不会校验这个 ID 是否存在。
- `remaining`：剩余量，必须大于或等于 `0`。
- `capacity`：容量上限，必须大于或等于 `0`，并且 `remaining` 不能大于 `capacity`。
- `consume_rate`：消耗速率，必须大于或等于 `0`。当前只保存该值，不按 tick 或事件自动扣除。

推荐约定：
- 原版或其他模组物品：`minecraft:diamond`、`othermod:custom_ingot`
- 液体：`minecraft:water`、`minecraft:lava`、`mekanism:heavy_water`
- Mekanism 气体：`mekanism:hydrogen`、`mekanism:oxygen`
- 能量：`mekanism:energy`

后续外部 JSON 或外部 Java 接口可以通过替换盔甲和插板的数据组件来改变 `type`、`remaining`、`capacity` 和 `consume_rate`。当前版本不负责“物品如何转化成这些数据量”，也不负责真实消耗。

Java 辅助入口：
```java
List<ConsumptionState> armorData = ConsumptionDataAccess.armorConsumption(stack);
ConsumptionDataAccess.setArmorConsumption(stack, List.of(
    new ConsumptionState(ResourceLocation.fromNamespaceAndPath("mekanism", "energy"), 9000, 10000, 20)
));
```

插板同理：
```java
List<ConsumptionState> pluginData = ConsumptionDataAccess.pluginConsumption(stack);
ConsumptionDataAccess.setPluginConsumption(stack, updatedData);
```

## 预定义效果 ID

当前内置效果：
- `armortemplatejson:elytra_flight`：四件同等级盔甲激活后，胸甲可作为鞘翅飞行。
- `armortemplatejson:creative_flight`：四件同等级盔甲激活后，给玩家 NeoForge creative flight 能力。
- `armortemplatejson:damage_reflection`：受到伤害后，把最终伤害反弹给攻击者。
- `armortemplatejson:powder_snow_walking`：可在细雪上行走。
- `armortemplatejson:piglin_neutral`：使猪灵保持中立。

套装激活规则：
- 必须同时穿戴头盔、胸甲、护腿、靴子四件由模板生成的盔甲。
- 四件盔甲的 `slot` 必须和实际装备槽对应。
- 四件盔甲的 `level` 必须相同。
- 激活后会合并盔甲效果、有效插板效果和有效插板组合效果。

## 注册自定义 Java 效果

外部 Java 代码可以注册自己的效果实现：
```java
ArmorEffectRegistry.register(
    ResourceLocation.fromNamespaceAndPath("example", "my_effect"),
    new ArmorTemplateEffect() {
        // 按需要覆盖 onPlayerTick、canElytraFly、onLivingDamagePost 等方法。
    }
);
```

JSON 中引用这个 ID 即可：
```json
"effect": "example:my_effect"
```

插板和组合效果中也可以引用：
```json
"effects": {
  "effect": "example:my_effect"
}
```

## 插板 UI 使用方法

1. 主手拿一件由模板生成、`slot` 为 `body`、且 `chestplate.plugin_numbers > 0` 的胸甲。
2. 按住潜行并右键打开插板界面。其他槽位的盔甲不会打开插板界面。
3. 上方是插板槽，下方是玩家背包。
4. 打开界面时，当前热键栏槽位会被锁定，防止编辑中的胸甲被移动。
5. 关闭界面时，服务端会把插板槽内容写回胸甲数据组件。

## 代码生成入口

生成盔甲物品：
```java
ItemStack armor = ArmorTemplateStackFactory.createStack(templateId, armorTemplate);
```

生成插板物品：
```java
ItemStack plugin = PluginStackFactory.createStack(pluginId, pluginDefinition);
```

## 用 IDLE 打开源码

项目保留了 IDLE 打开脚本：
```text
tools/open_sources_in_idle.py
```

如果桌面上存在 `OpenArmorTemplateJsonSourcesInIDLE.bat`，可以直接双击打开核心源码文件。
