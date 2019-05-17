package com.github.mob41.osums.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mob41.osumer.debug.WithDumpException;
import com.github.mob41.osums.Osums;
import com.github.mob41.osums.search.ResultBeatmap;
import com.github.mob41.osums.search.SearchingProgressHandler;

public class OnlineIndexManager {
    
    private static final String RANKED_INDEX = "https://old.ppy.sh/p/beatmaplist?l=1&r=0&q=&g=0&la=0&ra=&s=4&o=1&m=-1";
    
    private static final String QUALIFIED_INDEX = "https://old.ppy.sh/p/beatmaplist&s=4&r=11";
    
    private static final String LOVED_INDEX = "https://old.ppy.sh/p/beatmaplist&s=4&r=12";
    
    private static final String PENDING_INDEX = "https://old.ppy.sh/p/beatmaplist&s=7&r=2";

    //private static final String RANKED_INDEX = "https://osu.ppy.sh/p/beatmaplist?q=naruto&m=-1&r=0&g=0&la=0";
    
    private List<ResultBeatmap> indexes;
    
    private final String dataFilePath;
    
    private final String dataFileName;
    
    private final Osums osums;
    
    public OnlineIndexManager(String dataFilePath, String dataFileName, Osums osums) {
        this.osums = osums;
        
        this.dataFilePath = dataFilePath;
        this.dataFileName = dataFileName;
        
        indexes = new ArrayList<ResultBeatmap>();
    }
    
    public boolean isIndexed(){
        return indexes != null && indexes.size() > 0;
    }
    
    public ResultBeatmap[] searchDatabase(String queryString) throws WithDumpException{
        queryString = queryString.toLowerCase();
        
        List<ResultBeatmap> results = new ArrayList<ResultBeatmap>();
        ObjectMapper map = new ObjectMapper();
        
        for (int i = 0; i < indexes.size(); i++){
            /*
            ResultBeatmap map = ResultBeatmap.fromJson(arr.getJSONObject(i));
            if (map.getArtist().toLowerCase().contains(queryString) ||
                    map.getCreator().toLowerCase().contains(queryString) ||
                    //map.getGenre().contains(queryString) ||
                    //map.getName().contains(queryString) ||
                    //map.getOriginalUrl().contains(queryString) ||
                    //map.getSource().contains(queryString) ||
                    //map.getThumbUrl().contains(queryString) ||
                    map.getTitle().toLowerCase().contains(queryString) ||
                    queryString.contains(Integer.toString(map.getId()))){
                results.add(map);
            }
            */
            //System.out.println("Processing " + i);
            //System.out.println(indexes.get(i).getTitle());


            if (indexes.get(i).getArtist().toLowerCase().contains(queryString) ||
                    indexes.get(i).getCreator().toLowerCase().contains(queryString) ||
                    indexes.get(i).getTitle().toLowerCase().contains(queryString) ||
                    queryString.contains("" + indexes.get(i).getId())){
                results.add(indexes.get(i));
            }
        }
        
        ResultBeatmap[] out = new ResultBeatmap[results.size()];
        for (int i = 0; i < out.length; i++){
            out[i] = results.get(i);
        }
        
        return out;
    }
    
    public boolean startIndexing() throws WithDumpException{
        return startIndexing(new IndexingProgressAdapter());
    }
    
    private ResultBeatmap[] doMapSearch(IndexingProgressHandler handler, int lastIndexed, String url) throws WithDumpException{
        return osums.getLinksOfBeatmapSearch(new SearchingProgressHandler() {
            
            @Override
            public boolean onStart() {
                return true;
            }
            
            @Override
            public boolean onPause() {
                return true;
            }
            
            @Override
            public boolean onLoopStart() {
                handler.onLoopStart();
                return true;
            }
            
            @Override
            public boolean onLoopEnd() {
                handler.setBeatmapIndexed(lastIndexed + this.getBeatmapIndexed());
                handler.setCompletedPages(this.getCompletedPages());
                handler.setTotalPages(this.getTotalPages());
                handler.onLoopEnd();
                return true;
            }
            
            @Override
            public boolean onError() {
                handler.onError();
                return true;
            }
            
            @Override
            public boolean onComplete() {
                return true;
            }
        }, url);
    }
    
    public boolean startIndexing(IndexingProgressHandler handler) throws WithDumpException{
        handler.setMode(IndexingProgressHandler.SEARCHING_MAPS);
        handler.onStart();
        indexes.clear();
        
        int total = 0;
        
        System.out.println("==================Doing RANKED");
        ResultBeatmap[] smaps = doMapSearch(handler, total, RANKED_INDEX);
        
        total += smaps.length;
        for (ResultBeatmap map : smaps){
            indexes.add(map);
        }

        System.out.println("==================Doing QUALIFIED");
        smaps = doMapSearch(handler, total, QUALIFIED_INDEX);
        
        total += smaps.length;
        for (ResultBeatmap map : smaps){
            indexes.add(map);
        }

        System.out.println("==================Doing LOVED");
        smaps = doMapSearch(handler, total, LOVED_INDEX);
        
        total += smaps.length;
        for (ResultBeatmap map : smaps){
            indexes.add(map);
        }

        System.out.println("==================Doing PENDING");
        smaps = doMapSearch(handler, total, PENDING_INDEX);
        
        total += smaps.length;
        for (ResultBeatmap map : smaps){
            indexes.add(map);
        }
        /*
        JSONArray indexesArr = new JSONArray();
        
        for (int i = 0; i < smaps.length; i++){
            indexesArr.put(smaps[i].toJson());
        }
        
        json.put("indexes", indexesArr);
        */
        
        handler.onComplete();
        
        return true;
    }
    
    /*
    public JSONArray getIndexes(){
        return json;
    }
    */
    
    public void load() throws IOException {
        File file = new File(dataFilePath + "/" + dataFileName);
        if (!file.exists()) {
            write();
            return;
        }
        FileInputStream in = new FileInputStream(file);

        /*
        String data = "";
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        while ((line = reader.readLine()) != null) {
            data += line;
        }
        reader.close();
*/
        /* 
         * Preferences prefs = Preferences.userRoot();
         * 
         * String data = prefs.get("com.github.mob41.osumer.configuration",
         * null);
         * 
         * if (data == null){ write(); return; }
         */

        ObjectMapper map = new ObjectMapper();
        indexes = map.readValue(in, new TypeReference<ArrayList<ResultBeatmap>>(){});
    }

    public void write() throws IOException {

        File folder = new File(dataFilePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(dataFilePath + "/" + dataFileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        
        FileOutputStream out = new FileOutputStream(file);
        PrintWriter writer = new PrintWriter(out, true);
        
        ObjectMapper map = new ObjectMapper();
        writer.println(map.writeValueAsString(indexes));
        writer.close();
        
        out.flush();
        out.close();
    }
}
