/*    */ package com.flipturnapps.foldersync;
/*    */ 
/*    */ import java.io.File;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FolderSync
/*    */ {
/*    */   public static void hostSync(FolderSyncOutput out, File dir) {}
/*    */   
/*    */   public static void joinSync(FolderSyncOutput out) {}
/*    */   
/*    */   public static FolderSyncOutput joinSync()
/*    */   {
/* 18 */     FolderSyncOutput out = new SimpleFolderSyncOutput();
/* 19 */     joinSync(out);
/* 20 */     return out;
/*    */   }
/*    */   
/*    */   public static FolderSyncOutput hostSync(File dir)
/*    */   {
/* 25 */     FolderSyncOutput out = new SimpleFolderSyncOutput();
/* 26 */     hostSync(out, dir);
/* 27 */     return out;
/*    */   }
/*    */ }


/* Location:              C:\Users\Kevin\Downloads\s.jar!\com\flipturnapps\foldersync\FolderSync.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */