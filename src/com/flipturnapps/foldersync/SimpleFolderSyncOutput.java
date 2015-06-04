/*    */ package com.flipturnapps.foldersync;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ public class SimpleFolderSyncOutput
/*    */   extends FolderSyncOutput
/*    */ {
/*  8 */   private long time = 0L;
/*    */   
/*    */   protected void progress(double percent)
/*    */   {
/* 12 */     if (System.currentTimeMillis() - this.time > 5000L)
/*    */     {
/* 14 */       textOutput("Prog: " + percent);
/* 15 */       this.time = System.currentTimeMillis();
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   protected void textOutput(String output)
/*    */   {
/* 23 */     System.out.println("-FolderSync: " + output);
/*    */   }
/*    */ }


/* Location:              C:\Users\Kevin\Downloads\s.jar!\com\flipturnapps\foldersync\SimpleFolderSyncOutput.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */