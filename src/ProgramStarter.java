import IndexerAndDictionary.CountAndPointerDicValue;
import IndexerAndDictionary.Indexer;
import OuputFiles.DictionaryFileHandler;
import TermsAndDocs.Terms.Term;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ProgramStarter {

    /**
     * this method starts the main program by creating workers an executing them.
     * @param inputPath
     * @param outputPath
     * @param toStemm
     */
    public static void startProgram(String inputPath, String outputPath, boolean toStemm) {
        long start = System.currentTimeMillis();
        String pathFolder = inputPath + "\\corpus";
        String stemRelatedFolder = getStemRelatedFolder(toStemm);
        initFolders(toStemm,outputPath);
        File folder = new File(pathFolder);
        String[] folderFiles = folder.list();
        ThreadPoolExecutor executor;
        String[][] arrays = initWorkersArrays(folderFiles);
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool((Runtime.getRuntime().availableProcessors() + 2));
        HashSet<String> stopWords = readStopWords(inputPath + "\\05 stop_words");
        ArrayList<String> docsPath = new ArrayList<>();
        for (int i = 0; i < arrays.length; i++) {
            String[] readFilesPath = arrays[i];//302-305
            String sPostFilePath = stemRelatedFolder + "\\workersFiles\\workerArray" + i + "\\";
            String docFilePath = outputPath + "\\" + stemRelatedFolder + "\\DocsFiles\\docFile" + i;
            docsPath.add(docFilePath);
            WorkerThread wt = new WorkerThread(pathFolder, readFilesPath, sPostFilePath, docFilePath, stopWords,toStemm);
            executor.execute(wt);
        }
        try {
            executor.shutdown();
            executor.awaitTermination(200000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        HashSet<String> deletedTerms = Indexer.dictionary.deleteNotEntities();
        ConcurrentHashMap<Term, CountAndPointerDicValue> dic = Indexer.dictionary.dictionaryTable;
        long end = System.currentTimeMillis();

        HandleMerge handleMerge = new HandleMerge(deletedTerms, Indexer.dictionary, outputPath, toStemm);
        handleMerge.merge();

        DictionaryFileHandler dictionaryFileHandler = new DictionaryFileHandler(Indexer.dictionary);
        dictionaryFileHandler.writeToFile(outputPath, toStemm);


        System.out.println("dic: " + dic.size());
        System.out.println("time to read and parse all files: " + (end - start));

    }

    private static String getStemRelatedFolder(boolean toStemm) {
        if (toStemm)
            return "stemOur";
        return "noStemOur";
    }

    /**
     * this is a static method which getting
     * @param path
     * @return hashSet of all the sth given stop words
     */
    private static HashSet<String> readStopWords(String path) {
        File file = new File(path);
        BufferedReader br;
        HashSet<String> stopWords = new HashSet<>();
        try {
            br = new BufferedReader(new FileReader(file + ".txt"));
            String st;
            while ((st = br.readLine()) != null) {
                if(!st.equals(""))
                    stopWords.add(st);
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopWords;
    }

    /**
     * this method inits the files array that we are sending to each worker
     * @param folderFiles
     * @return
     */
    private static String[][] initWorkersArrays(String[] folderFiles) {
        int n = folderFiles.length;
        String[] aFilesArray = new String[(n + 1) / 6];//gonna hold 302 files
        String[] bFilesArray = new String[(n + 1) / 6];//gonna hold 302 files
        String[] cFilesArray = new String[(n + 1) / 6];//gonna hold 302 files
        String[] dFilesArray = new String[(n + 1) / 6];//gonna hold 302 files
        String[] eFilesArray = new String[(n + 1) / 6];//gonna hold 302 files
        String[] fFilesArray = new String[n - (5 * aFilesArray.length)];//gonna hold 305

        System.arraycopy(folderFiles, 0, aFilesArray, 0, aFilesArray.length);
        System.arraycopy(folderFiles, aFilesArray.length, bFilesArray, 0, bFilesArray.length);
        System.arraycopy(folderFiles, 2 * aFilesArray.length, cFilesArray, 0, cFilesArray.length);
        System.arraycopy(folderFiles, 3 * aFilesArray.length, dFilesArray, 0, dFilesArray.length);
        System.arraycopy(folderFiles, 4 * aFilesArray.length, eFilesArray, 0, eFilesArray.length);
        System.arraycopy(folderFiles, 5 * aFilesArray.length, fFilesArray, 0, fFilesArray.length);
        String[][] arrays = {aFilesArray, bFilesArray, cFilesArray, dFilesArray, eFilesArray, fFilesArray};
        return arrays;
    }

    /**
     * this method makes the folders we need if they dont exist
     * @param toStem
     * @param outputPath
     */
    private static void initFolders(boolean toStem, String outputPath) {
        //String sPostFilePath = stemRelatedFolder + "\\workersFiles\\workerArray" + i + "\\";
        //String docFilePath = outputPath + "\\" + stemRelatedFolder + "\\tempDocsFiles\\docFile" + i;

        String stemWorkersDirPath = getStemRelatedFolder(toStem);
        File stemWorkerDir = new File(stemWorkersDirPath);
        if (!stemWorkerDir.exists()) {
            stemWorkerDir.mkdir();
        }

        stemWorkersDirPath = stemWorkersDirPath + "\\workersFiles";
        File stemWorkingDir = new File(stemWorkersDirPath);
        if (!stemWorkingDir.exists()) {
            stemWorkingDir.mkdir();
        }

        for (int i = 0; i < 6; i++) {
            String path = stemWorkersDirPath + "\\workerArray" + i;
            File workerArrayDir = new File(path);
            if (!workerArrayDir.exists()) {
                workerArrayDir.mkdir();
            }
        }

        String tempDocFilesPath = outputPath + "\\" + getStemRelatedFolder(toStem);
        File tempDocFiles = new File(tempDocFilesPath);
        if (!tempDocFiles.exists()) {
            tempDocFiles.mkdir();
        }

        tempDocFilesPath = outputPath + "\\" + getStemRelatedFolder(toStem) + "\\" + "tempDocsFiles";
        File fileTooCheck = new File(tempDocFilesPath);
        if (!fileTooCheck.exists()) {
            fileTooCheck.mkdir();
        }

       // for (int i = 0; i < 6; i++) {
       //     String path = tempDocFilesPath + "\\docFile" + i;
       //     File workerArrayDir = new File(path);
       //     if (!workerArrayDir.exists()) {
       //         workerArrayDir.mkdir();
       //     }
       // }



    }

}