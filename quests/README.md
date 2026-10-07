# 任务书导入

两条任务线，GTNH 的 BetterQuesting 格式（`config/betterquesting/DefaultQuests`）：

| 目录前缀 | 任务线 | 需要 |
|---|---|---|
| `LazyAE-` | 私货 · 懒人AE | AE2、ae2fc（GTNH 自带） |
| `FluxDepthsShards-` | 通量深层 · 碎片采集器 | FluxDepths 模组 |

## 用脚本导入（推荐）

PowerShell：

```powershell
.\quests\install.ps1 -Instance "E:\Game\HMCL\.minecraft\versions\GT New Horizons 2.8.4"
```

- 加 `-Line lazyae` 或 `-Line shards` 只导入其中一条。
- 脚本先把原来的 `DefaultQuests` 备份成 `DefaultQuests.backup-时间`。
- 然后复制任务文件，并在 `QuestLinesOrder.txt` 末尾加上任务线。
- 重复运行不会重复添加。

进游戏后在聊天栏执行：

```
/bq_admin default load
```

已有的任务进度会保留。

## 手动导入

1. 把 `DefaultQuests/Quests/` 和 `DefaultQuests/QuestLines/` 里对应的文件夹复制到实例的 `config/betterquesting/DefaultQuests/` 下对应位置。
2. 把 `DefaultQuests/QuestLinesOrder.add.txt` 里对应的行追加到实例的 `QuestLinesOrder.txt` 末尾。
3. 进游戏执行 `/bq_admin default load`。

## 注意

- GTNH 整合包更新时会整个替换 `DefaultQuests`，更新后再运行一次脚本即可。任务 ID 固定，进度不会丢。
- 懒人AE 的三个任务是勾选即领。领取前先清空背包，放不下的物品会掉在地上。
- 改任务内容请改 `build_quests.py`，然后执行 `python quests/build_quests.py` 重新生成。
