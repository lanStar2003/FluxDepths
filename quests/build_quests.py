"""
Writes the FluxDepths quest lines in GTNH's BetterQuesting layout (config/betterquesting/DefaultQuests).
Run from the repository root: python quests/build_quests.py
Output: quests/DefaultQuests/{QuestLines,Quests}/... and quests/DefaultQuests/QuestLinesOrder.add.txt
Quest ids are derived from fixed keys, so running it again keeps the same ids (and the players' progress).
"""
import base64
import json
import os
import shutil
import struct
import uuid

OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "DefaultQuests")
NS = uuid.UUID("6f1c2d6a-3a0b-4b51-9a57-7f1d2e9c4b10")
FIRST_ID = 24520  # shard_collectors.firstMachineId


def ids(key):
    u = uuid.uuid5(NS, key)
    hi, lo = struct.unpack(">qq", u.bytes)
    return hi, lo, base64.urlsafe_b64encode(u.bytes).decode()


def item(i, dmg=0, count=1, ore=""):
    return {"Count:3": count, "Damage:2": dmg, "OreDict:8": ore, "id:8": i}


def numbered(entries):
    return {f"{n}:10": e for n, e in enumerate(entries)}


def stacks(i, dmg, count):
    out = []
    while count > 0:
        out.append(item(i, dmg, min(64, count)))
        count -= 64
    return out


AE = "appliedenergistics2:"
PART = AE + "item.ItemMultiPart"
MAT = AE + "item.ItemMultiMaterial"


def quest(key, name, desc, icon, tasks, rewards=(), pre=(), main=True):
    hi, lo, b64 = ids(key)
    t = []
    for n, task in enumerate(tasks):
        task = dict(task)
        task["index:3"] = n
        t.append(task)
    r = []
    for n, rew in enumerate(rewards):
        rew = dict(rew)
        rew["index:3"] = n
        r.append(rew)
    q = {}
    if pre:
        q["preRequisites:9"] = numbered([{"questIDHigh:4": ids(p)[0], "questIDLow:4": ids(p)[1]} for p in pre])
    q["properties:10"] = {"betterquesting:10": {
        "autoClaim:1": 0, "desc:8": desc, "globalShare:1": 1, "icon:10": icon, "isGlobal:1": 0,
        "isMain:1": 1 if main else 0, "isSilent:1": 0, "lockedProgress:1": 0, "name:8": name,
        "partySingleReward:1": 0, "questLogic:8": "AND", "repeatTime:3": -1, "repeat_relative:1": 1,
        "simultaneous:1": 0, "snd_complete:8": "random.levelup", "snd_update:8": "random.levelup",
        "taskLogic:8": "AND", "visibility:8": "NORMAL"}}
    q["questIDHigh:4"] = hi
    q["questIDLow:4"] = lo
    q["rewards:9"] = numbered(r)
    q["tasks:9"] = numbered(t)
    return key, q, b64


def checkbox():
    return {"taskID:8": "bq_standard:checkbox"}


def retrieval(*items, ignore_nbt=False):
    return {"autoConsume:1": 0, "consume:1": 0, "groupDetect:1": 0, "ignoreNBT:1": 1 if ignore_nbt else 0,
            "partialMatch:1": 1, "requiredItems:9": numbered(list(items)), "taskID:8": "bq_standard:retrieval"}


def give(*items):
    flat = []
    for i in items:
        flat.extend(i if isinstance(i, list) else [i])
    return {"ignoreDisabled:1": 0, "rewardID:8": "bq_standard:item", "rewards:9": numbered(flat)}


def dump(path, obj):
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(obj, f, ensure_ascii=False, indent=2)
        f.write("\n")


def write_line(key, slug, name, desc, icon, placed):
    hi, lo, b64 = ids(key)
    line_dir = f"{slug}-{b64}"
    qdir = os.path.join(OUT, "Quests", line_dir)
    ldir = os.path.join(OUT, "QuestLines", line_dir)
    os.makedirs(qdir, exist_ok=True)
    os.makedirs(ldir, exist_ok=True)
    dump(os.path.join(ldir, "QuestLine.json"), {
        "properties:10": {"betterquesting:10": {"bg_image:8": "", "bg_size:3": 256, "desc:8": desc,
                                                "icon:10": icon, "name:8": name, "visibility:8": "NORMAL"}},
        "questLineIDHigh:4": hi, "questLineIDLow:4": lo})
    for (qkey, q, qb64), (x, y) in placed:
        stem = "".join(c for c in qkey.split("/")[-1].title() if c.isalnum())[:16]
        fname = f"{stem}-{qb64}.json"
        dump(os.path.join(qdir, fname), q)
        dump(os.path.join(ldir, fname), {"sizeX:3": 24, "sizeY:3": 24, "x:3": x, "y:3": y,
                                          "questIDHigh:4": q["questIDHigh:4"], "questIDLow:4": q["questIDLow:4"]})
    return f"{b64}: {name}"


# ---------------------------------------------------------------- lazy AE

def lazy_ae():
    core = quest(
        "lazyae/core", "§b§l懒人AE · 核心与存储",
        "一套能一直用到 EV 的 AE 网络核心。\n\n"
        "[note]ME 自供能控制器自带无限能源：不用能源接收器，也不用谐振仓，放下就有电。[/note]\n\n"
        "怎么搭：\n"
        "1. 放下自供能控制器，从它的面上接致密线缆当主干，一面最多 32 个频道。\n"
        "2. 两台 ME 驱动器插上存储元件：物品用 64k / 16k，流体用流体元件（蒸汽、杂酚油、水都能存）。\n"
        "3. 合成终端看库存、直接合成；样板终端和增广流体样板终端写样板；接口终端统一管理所有接口里的样板。\n\n"
        "[warn]领取前先清空背包，放不下的会掉在地上。[/warn]",
        item(AE + "tile.BlockCreativeEnergyController"),
        [checkbox()],
        [give(item(AE + "tile.BlockCreativeEnergyController", 0, 2), item(AE + "tile.BlockDrive", 0, 2),
              item(AE + "item.ItemBasicStorageCell.64k", 0, 6), item(AE + "item.ItemBasicStorageCell.16k", 0, 4),
              item("ae2fc:fluid_storage64", 0, 2), item("ae2fc:fluid_storage16", 0, 2),
              item(PART, 360), item(PART, 340), item("ae2fc:part_fluid_pattern_terminal_ex"), item(PART, 480),
              item("ae2fc:part_fluid_terminal"), item(AE + "item.ToolNetworkTool"),
              item(AE + "item.ToolMemoryCard", 0, 2))])
    crafting = quest(
        "lazyae/crafting", "§b§l懒人AE · 自动合成",
        "让 AE 替你干活。\n\n"
        "• 工作台配方：在样板终端里用「合成」模式写样板，放进 ME 接口，接口贴着分子装配室。要 GT 工具的配方也行，工具磨损后会还回网络。\n"
        "• 机器配方（蒸汽打粉机、锻造锤、合金炉……）：用「处理」模式写样板，放进 ME 二合一接口，二合一接口贴着机器并打开「阻挡模式」；"
        "机器的另一面贴一个 ME 输入总线，把产物抽回网络。\n"
        "• 要流体的配方用增广流体样板终端写，二合一接口会把流体一起送进机器。\n"
        "• 合成 CPU：拼成 2×2×2，每组放 1 个 64k 和 1 个 16k 合成存储器、4 个并行处理单元、1 个合成单元、1 个合成监控器（能看进度）。这里给了两组的料。\n\n"
        "[note]加速卡插进输入 / 输出总线会快很多；样板扩容卡让一个接口放更多样板；合成卡让输出总线缺货时自动下单。[/note]",
        item(AE + "tile.BlockMolecularAssembler"),
        [checkbox()],
        [give(item(AE + "tile.BlockMolecularAssembler", 0, 12), item("ae2fc:fluid_interface", 0, 12),
              item(AE + "tile.BlockInterface", 0, 4), item(AE + "tile.BlockCraftingStorage", 3, 2),
              item(AE + "tile.BlockCraftingStorage", 2, 2), item(AE + "tile.BlockCraftingUnit", 1, 8),
              item(AE + "tile.BlockCraftingUnit", 0, 2), item(AE + "tile.BlockCraftingMonitor", 0, 2),
              stacks(MAT, 52, 192), item(MAT, 30, 24), item(MAT, 53, 8), item(MAT, 27, 8), item(MAT, 54, 8),
              item(MAT, 29, 4), item(MAT, 26, 4))])
    cables = quest(
        "lazyae/cables", "§b§l懒人AE · 线缆与输入输出",
        "把网络接到每一台机器上。\n\n"
        "• 玻璃 / 包层线缆走 8 个频道，智能线缆能看到频道占用，致密线缆走 32 个。控制器每一面出 32 个频道，用致密线缆做主干，再分出普通线缆。\n"
        "• 存储总线贴在箱子、抽屉、储罐上，网络就能直接读写它们。\n"
        "• 输入总线把机器的产物抽回网络，输出总线把物品推出去；流体用对应的流体总线。\n"
        "• ME 标准发信器：库存低于设定值时输出红石，可以用来开关锅炉、机器。\n"
        "• P2P 通道能把一整束频道送到远处。\n\n"
        "[note]GTNH 里 AE 要到 EV 才能自己做，这套的量就是按撑到那时准备的。[/note]",
        item(PART, 56),
        [checkbox()],
        [give(stacks(PART, 16, 128), item(PART, 36, 64), item(PART, 56, 64), item(PART, 76, 16), item(PART, 140, 16),
              item(PART, 240, 8), item(PART, 260, 8), item(PART, 220, 8), item(PART, 280, 4),
              item("ae2fc:part_fluid_import", 0, 4), item("ae2fc:part_fluid_export", 0, 4),
              item("ae2fc:part_fluid_storage_bus", 0, 4), item(PART, 460, 4))])
    return write_line(
        "line/lazyae", "LazyAE", "§b私货 · 懒人AE",
        "开局直接领一整套 AE2，蒸汽时代就能全自动。\n\n"
        "三个任务都是勾选即领，没有前置。GTNH 正常要到 EV 才能自己做 AE，这套的量够你撑到那时。",
        item(AE + "tile.BlockCreativeEnergyController"),
        [(core, (0, 0)), (crafting, (48, 0)), (cables, (96, 0))])


# ---------------------------------------------------------------- shard collectors

def machine(tier):
    return item("gregtech:gt.blockmachines", FIRST_ID + tier)


def shards():
    lore = quest(
        "shards/depths", "§3§l通量深层",
        "FluxLite 的网络把电送进「通量层」——现实之下、贯穿所有世界的一层能量之海。\n\n"
        "通量层再往下，是「通量深层」。那里沉着世界诞生时留下的碎片：每个世界生成时，地下的每一条矿脉，都是深层里一块碎片的投影。"
        "这也是为什么 GT 的矿脉总是整整齐齐地每 3 个区块出现一次——那是碎片投进现实的网格。\n\n"
        "你挖空一条矿脉，碎片还在，只是不再投影。\n\n"
        "碎片采集器拿着一张「矿脉印记」对准深层里的那块碎片，让它再投影一次：不是投进地底，而是投进采集器的腔体里，一次凝结一块矿石。"
        "投影很微弱，所以很慢；采集器等级越高，共振越稳，速度越快。\n\n"
        "[note]为什么永远比不上原版的虚空采矿机？虚空采矿机是把整个世界的通量层撕开一道口子——所以出来的是全世界所有矿脉的混合物，"
        "要 LuV 的电，还要惰性气体撑住裂口。采集器只是透过针孔聆听一块碎片：最快的大师级（IV）也只有虚空采矿机 I 的 77%。[/note]",
        item("fluxdepths:imprint"),
        [checkbox()])
    imprinter = quest(
        "shards/imprinter", "§3印记拓印器",
        "一根带透镜的青铜杆，能听见脚下矿脉在深层里的回响。\n\n"
        "站在矿脉所在的区域里（以矿脉为中心的 3×3 区块）右键，就会拓下一张矿脉印记，每次消耗一张纸。\n\n"
        "[note]它只认世界生成时真实存在的矿脉——和 VisualProspecting 地图上记录的一样，所以得先找到矿脉。[/note]",
        item("fluxdepths:imprinter"),
        [retrieval(item("fluxdepths:imprinter"))],
        [give(item("minecraft:paper", 0, 16))],
        pre=["shards/depths"])
    first = quest(
        "shards/first", "§3第一张矿脉印记",
        "先找矿脉：\n"
        "• 用 GT 的探矿工具，或者打开地图（JourneyMap / Navigator）看 VisualProspecting 已经记下的矿脉；\n"
        "• 走到矿脉的范围里（水平位置在它的 3×3 区块之内，高度不限），拿着拓印器右键。\n\n"
        "印记上写着这条矿脉出哪些矿石、各占多少，以及它来自哪个世界。\n\n"
        "[warn]印记只在拓下它的那个世界里有效：主世界的印记在下界、月球都不会共振。外星的矿脉要先飞过去拓印，再在当地建采集器。[/warn]\n\n"
        "[note]NEI 里搜「碎片采集」能看到每条矿脉的产出；查某种矿石的来源，也能看到它在哪些矿脉里。[/note]",
        item("fluxdepths:imprint"),
        [retrieval(item("fluxdepths:imprint"), ignore_nbt=True)],
        pre=["shards/imprinter"])
    steam = quest(
        "shards/steam", "§6蒸汽碎片采集器",
        "第一台采集器：青铜外壳，烧低压蒸汽。\n\n"
        "• 矿脉印记放进印记槽（带数据棒图标的那一格）；\n"
        "• 输入槽放一个 GT 青铜钻头（或更好的）当开孔头，一个能用 128 次；\n"
        "• 每 10 秒凝结 1 块矿石（360 块 / 小时），工作时消耗 16 L/t 蒸汽；\n"
        "• 和 GT 的蒸汽机器一样，每块矿石完成后都要从排气口放汽，排气口前面不能堵；\n"
        "• 它不会自动输出，用漏斗或 AE 的输入总线把矿石取走。\n\n"
        "出来的是普通矿石方块，后面照常打粉、洗矿。",
        machine(0), [retrieval(machine(0))], pre=["shards/first"])
    hp = quest(
        "shards/hp_steam", "§6高压蒸汽碎片采集器",
        "钢外壳，高压蒸汽：每 5 秒 1 块（720 块 / 小时），32 L/t 蒸汽。\n\n"
        "开孔头至少要钢钻头（256 次）。用一台蒸汽碎片采集器升级而来。",
        machine(1), [retrieval(machine(1))], pre=["shards/steam"])
    lv = quest(
        "shards/lv", "§7基础碎片采集器（LV）",
        "进入电力时代：每 2.5 秒 1 块（1440 块 / 小时），24 EU/t。\n\n"
        "• 能放 2 张印记（印记槽 + 一个输入槽），两条矿脉轮流产出——是分享，不是叠加；\n"
        "• 开孔头：钢钻头或更好；\n"
        "• 用扳手设一个输出面，就能自动把矿石推出去。\n\n"
        "[note]可以用 FluxLite 连接器从无线电网给它供电。[/note]",
        machine(2), [retrieval(machine(2))], pre=["shards/hp_steam"])
    mv = quest(
        "shards/mv", "§b进阶碎片采集器（MV）",
        "每 1.65 秒 1 块（约 2180 块 / 小时），96 EU/t。\n\n"
        "从这一级起，针孔要用钻井液冷却：每块矿石消耗 20 L。钻井液在搅拌机里做（配方看 NEI）。\n\n"
        "开孔头：铝钻头或更好（384 次）。",
        machine(3), [retrieval(machine(3))], pre=["shards/lv"])
    hv = quest(
        "shards/hv", "§6高级碎片采集器（HV）",
        "每 1.25 秒 1 块（2880 块 / 小时），384 EU/t，能放 3 张印记。\n\n开孔头：不锈钢钻头或更好（512 次）。",
        machine(4), [retrieval(machine(4))], pre=["shards/mv"])
    ev = quest(
        "shards/ev", "§5精英碎片采集器（EV）",
        "每 0.9 秒 1 块（4000 块 / 小时），1536 EU/t，能放 3 张印记。\n\n开孔头：钛钻头或更好（768 次）。",
        machine(5), [retrieval(machine(5))], pre=["shards/hv"])
    iv = quest(
        "shards/iv", "§1大师碎片采集器（IV）",
        "每 0.65 秒 1 块（约 5540 块 / 小时），6144 EU/t，能放 4 张印记。\n\n"
        "开孔头：钨钢钻头（1024 次）。\n\n这是针孔能做到的极限。",
        machine(6), [retrieval(machine(6))], pre=["shards/ev"])
    handover = quest(
        "shards/handover", "§3交接：虚空采矿机",
        "再往上，就该把通量层撕开了。\n\n"
        "原版的虚空采矿机（LuV 起）一次拿整个世界所有矿脉的混合，每秒 2 块起步，加上惰性气体能到每秒上百块。"
        "采集器的使命到此为止，剩下的交给它。\n\n"
        "[note]采集器不会因此作废：想专门刷某一条矿脉（只要铂、只要钍……），印记依然是最准的办法。[/note]",
        machine(6),
        [checkbox()], pre=["shards/iv"], main=False)
    placed = [(lore, (0, 48)), (imprinter, (48, 24)), (first, (96, 24)), (steam, (144, 0)), (hp, (192, 0)),
              (lv, (240, 0)), (mv, (288, 0)), (hv, (288, 48)), (ev, (240, 48)), (iv, (192, 48)),
              (handover, (144, 48))]
    return write_line(
        "line/shards", "FluxDepthsShards", "§3通量深层 · 碎片采集器",
        "拓下一条矿脉的印记，在家里慢慢回响出它的矿石。从蒸汽时代一路到 IV，直到原版的虚空采矿机接手。",
        item("fluxdepths:imprint"), placed)


if __name__ == "__main__":
    # GTNH names a quest line after its id as url-safe base64 of the two longs; check against one of its own
    probe = struct.pack(">qq", 3630150074513574271, -7072631871726141045)
    assert base64.urlsafe_b64encode(probe).decode() == "MmDiQmi9SX-d2PbI-qhBiw==", "id format changed"
    if os.path.isdir(OUT):
        shutil.rmtree(OUT)
    order = [lazy_ae(), shards()]
    with open(os.path.join(OUT, "QuestLinesOrder.add.txt"), "w", encoding="utf-8", newline="\n") as f:
        f.write("\n".join(order) + "\n")
    print("\n".join(order))
