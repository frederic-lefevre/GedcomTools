package org.fl.gedcomtools.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MediaSet {

	private static int INITIAL_CAPACITY = 10000;
	
	private Logger mLog;
	
	private HashMap<Path, String> mediaSet;
	
	public MediaSet(Logger l) {
		mLog = l;
		mediaSet = new HashMap<>(INITIAL_CAPACITY);
	}

	public void addMedia(Path mediaPath) {
		mediaSet.put(mediaPath.normalize(), mediaPath.getFileName().toString());
	}
	
	public List<Path> getUnreferencedMedias(Path rootPath) {
		
		try {
			return Files.walk(rootPath)
				.filter(Files::isRegularFile)
				.filter(path -> 
					(! mediaSet.containsKey(path.normalize())) && 
					(! mediaSet.entrySet().stream().anyMatch(mediaEntry -> {
						try {
							String fileName = path.getFileName().toString();
							if (mediaEntry.getValue().equals(fileName)) {
								// Same file name, really test now (slow operation)
								return Files.isSameFile(path, mediaEntry.getKey());
							} else {
								// not the same file, continue
								return false;
							}
						} catch (IOException e) {
							mLog.log(Level.SEVERE, "IOEception calling Files.isSameFile with \np1=" + path + "\np2=" + mediaEntry.getKey(), e);
							return false;
						}
					}))
				)
				.collect(Collectors.toList());
		} catch (IOException e) {
			mLog.log(Level.SEVERE, "IOEception walking folder: " + rootPath, e);
			return null;
		}
	}
}
