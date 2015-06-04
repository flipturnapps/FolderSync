/*    */ package com.flipturnapps.foldersync;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.net.URL;
/*    */ import java.net.UnknownHostException;
/*    */ import java.security.CodeSource;
/*    */ import java.security.ProtectionDomain;
/*    */ 
/*    */ public class Main
/*    */ {
/*    */   public static void main(String[] args)
/*    */   {
/* 14 */     if (args.length == 0)
/*    */     {
/* 16 */       c("127.0.0.1");
/*    */ 
/*    */ 
/*    */     }
/* 20 */     else if (args[0].startsWith("s"))
/*    */     {
/* 22 */       s(args[0].substring(1).split("?")[0], args[0].substring(1).split("?")[1], args[0].substring(1).split("?")[2]);
/*    */     }
/*    */     else
/*    */     {
/* 26 */       c(args[0]);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static void s(String folder, String count, String time)
/*    */   {
/* 35 */     new Thread(new FSHost(new SimpleFolderSyncOutput(), new File(getStartDir().getAbsolutePath() + "/" + folder + "/"), Long.parseLong(time), Integer.parseInt(count), 23452)).start();
/*    */   }
/*    */   
/*    */   public static File getStartDir() {
/* 39 */     String out = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath();
/* 40 */     File outfile = new File(out);
/* 41 */     File parFile = new File(outfile.getParent().replace("%20", " "));
/* 42 */     return parFile;
/*    */   }
/*    */   
/*    */   public static void c(String ip) {
/*    */     try {
/* 47 */       new Thread(new FSClient(ip, 23452, new SimpleFolderSyncOutput()));
/*    */     }
/*    */     catch (UnknownHostException e) {
/* 50 */       e.printStackTrace();
/*    */     }
/*    */     catch (IOException e) {
/* 53 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Kevin\Downloads\s.jar!\com\flipturnapps\foldersync\Main.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */