package pama1234.app.game.server.duel.util.ai.mesh;

import pama1234.app.game.server.duel.GetRandom     ;
import pama1234.app.game.server.duel.util.player.ServerPlayerActor;

/**
 * 陪练 AI：只射普通箭，不使用大招（长弓）。
 * 继承 DefaultPlayerPlan，重写 killPlan 使其永不触发 KillPlayerPlan（大招）。
 */
public final class SparringPlayerPlan extends DefaultPlayerPlan{
  public SparringPlayerPlan(GetRandom rng,float level) {
    super(rng,level);
  }
  @Override
  public PlayerPlan killPlan() {
    // 陪练模式：永不发大招，始终退回普通箭/移动
    return jabPlan();
  }
  @Override
  public PlayerPlan nextPlan(ServerPlayerActor player) {
    // 复用父类逻辑，但把 killPlan 分支替换为普通射箭
    return super.nextPlan(player);
  }
}