# 任务书导入

两条任务线，GTNH 的 BetterQuesting 格式（`config/betterquesting/DefaultQuests`）：

| 目录前缀 | 任务线 | 需要 |
|---|---|---|
| `LazyAE-` | 私货 · 懒人AE | AE2、ae2fc（GTNH 自带） |
| `FluxDepthsShards-` | 通量深层 · 碎片采集器 | FluxDepths 模组 |

## 导入

1. 把这里的 `DefaultQuests` 文件夹复制到实例的 `config/betterquesting/` 下，遇到同名文件选覆盖。
2. 进游戏后在聊天栏执行：

   ```
   /bq_admin default load
   ```

已有的任务进度会保留。以后任务有新增或修改，再复制覆盖一次、再执行一次 `/bq_admin default load` 就行。

## 注意

- `DefaultQuests/QuestLinesOrder.txt` 是 **GTNH 2.8.4** 原版的任务线顺序，末尾加了这两条。BetterQuesting 只加载这个文件里列出的任务线，所以它必须一起复制过去。
- 换了别的 GTNH 版本，不要覆盖这个文件，改为把它最后两行追加到实例自己的 `QuestLinesOrder.txt` 末尾。
- GTNH 整合包更新时会整个替换 `DefaultQuests`，更新后再导入一次即可。任务 ID 固定，进度不会丢。
- 懒人AE 的三个任务是勾选即领。领取前先清空背包，放不下的物品会掉在地上。
- 改任务内容请改 `build_quests.py`，然后执行 `python quests/build_quests.py` 重新生成。GTNH 原版的任务线顺序取自 `QuestLinesOrder.gtnh.txt`，换 GTNH 版本时用新版本的 `QuestLinesOrder.txt` 替换它。
