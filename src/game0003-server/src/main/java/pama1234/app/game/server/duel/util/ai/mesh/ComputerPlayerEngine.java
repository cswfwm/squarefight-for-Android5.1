package pama1234.app.game.server.duel.util.ai.mesh;

import pama1234.app.game.server.duel.GetRandom;
import pama1234.app.game.server.duel.util.player.PlayerEngine;
import pama1234.app.game.server.duel.util.player.ServerPlayerActor;

public final class ComputerPlayerEngine extends PlayerEngine{
  public GetRandom rng;
  public int time;
  public final int planUpdateFrameCount=10;
  public PlayerPlan currentPlan;
  public float level;
  public ComputerPlayerEngine(GetRandom rng,float level) {
    this(rng,level,false);
  }
  public ComputerPlayerEngine(GetRandom rng,float level,boolean sparring) {
    this.rng=rng;
    this.level=level;
    this.sparring=sparring;
    // There shoud be a smarter way!!!
    final MovePlayerPlan move=new MovePlayerPlan(rng,level);
    final JabPlayerPlan jab=new JabPlayerPlan(rng,level);
    final PlayerPlan kill;
    if(sparring) kill=new SparringPlayerPlan(rng,level);
    else kill=new KillPlayerPlan(rng,level);
    move.movePlan=move;
    move.jabPlan=jab;
    move.killPlan=kill;
    jab.movePlan=move;
    jab.jabPlan=jab;
    jab.killPlan=kill;
    if(kill instanceof KillPlayerPlan) ((KillPlayerPlan)kill).movePlan=move;
    if(kill instanceof DefaultPlayerPlan) {
      ((DefaultPlayerPlan)kill).movePlan=move;
      ((DefaultPlayerPlan)kill).jabPlan=jab;
    }
    currentPlan=move;
  }
  public final boolean sparring;
  @Override
  public void run(ServerPlayerActor player) {
    currentPlan.execute(player,inputDevice);
    if(time%planUpdateFrameCount==0) currentPlan=currentPlan.nextPlan(player);
    time++;
  }
  @Override
  public void setScore(int scoreType,float score) {
    //TODO nop
  }
  @Override
  public float getScore(int index) {
    return 0; //TODO nop
  }
}