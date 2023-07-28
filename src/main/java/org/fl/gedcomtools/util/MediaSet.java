/*
 * MIT License

Copyright (c) 2017, 2023 Frederic Lefevre

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package org.fl.gedcomtools.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.fl.gedcomtools.Config;

public class MediaSet {

	private static int INITIAL_CAPACITY = 10000;
	
	private static Logger mLog = Config.getLogger();
	
	private HashMap<Path, String> mediaSet;
	
	public MediaSet() {
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
