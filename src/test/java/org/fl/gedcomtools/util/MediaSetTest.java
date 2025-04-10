/*
 * MIT License

Copyright (c) 2017, 2024 Frederic Lefevre

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

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.fl.util.FilterCounter;
import org.fl.util.FilterCounter.LogRecordCounter;
import org.junit.jupiter.api.Test;

class MediaSetTest {
	
	@Test
	void shouldThrowNpeWhenNullRootPath() {
		
		MediaSet mediaSet = new MediaSet();
		assertThatNullPointerException().isThrownBy(() -> mediaSet.getUnreferencedMedias(null));
	}
	
	@Test
	void shouldReturnNullWhenUnexistantRootPath() {
		
		LogRecordCounter logFilterCounter = FilterCounter.getLogRecordCounter(Logger.getLogger(MediaSet.class.getName()));
		
		MediaSet mediaSet = new MediaSet();
		
		Path rootPath = Paths.get("/does/not/exists");
		List<Path> extraPath = mediaSet.getUnreferencedMedias(rootPath);
		assertThat(extraPath).isNull();
		
		assertThat(logFilterCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logFilterCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
	}
	
	@Test
	void shouldReturnFullListWhenMediaSetIsEmpty() {
		
		MediaSet mediaSet = new MediaSet();
		
		Path rootPath = Paths.get("/ForTests/BackUpFiles");
		List<Path> extraPaths = mediaSet.getUnreferencedMedias(rootPath);
		assertThat(extraPaths).isNotNull().isNotEmpty();
		
		try {
			assertThat(Files.walk(rootPath).filter(Files::isRegularFile).allMatch(path -> extraPaths.stream().anyMatch(extraPath -> {
				try {
					return Files.isSameFile(path, extraPath);
				} catch (IOException e) {
					fail("exception in isSameFile", e);
					return false;
				}
			})))
			.isTrue();
		} catch (IOException e) {
			fail("exception walking root path", e);
		}
	}
	
	@Test
	void shouldReturnAllButOneListWhenMediaExists() {
		
		MediaSet mediaSet = new MediaSet();
		
		Path rootPath = Paths.get("/ForTests/BackUpFiles");
		
		mediaSet.addMedia(Paths.get("C:\\ForTests\\BackUpFiles\\TestDataForMultiThread\\Concert/a/LutherAllison19831130.json"));
		List<Path> extraPath = mediaSet.getUnreferencedMedias(rootPath);
		assertThat(extraPath).isNotNull().isNotEmpty();
		
		try {
			List<Path> mediaPaths = Files.walk(rootPath).filter(Files::isRegularFile).collect(Collectors.toList());
			assertThat(mediaPaths).hasSize(extraPath.size() + 1);
		} catch (IOException e) {
			fail("exception walking root path", e);
		}
	}
	
	@Test
	void shouldReturnEmptyListWhenAllMediaAreFound() {
		
		MediaSet mediaSet = new MediaSet();
		
		Path rootPath = Paths.get("C:\\ForTests\\org.fl.gedcomtools\\mediaTestFolder");
		mediaSet.addMedia(Paths.get("C:\\ForTests\\org.fl.gedcomtools\\mediaTestFolder\\1750_1799\\1705_1709/AntoinetteGillet1707N.jpg"));
		mediaSet.addMedia(Paths.get("C:\\ForTests\\org.fl.gedcomtools\\mediaTestFolder\\1750_1799\\1705_1709/MathieuGuerinMarieBeuzeville1705M.jpg"));
		mediaSet.addMedia(Paths.get("C:\\ForTests\\org.fl.gedcomtools\\mediaTestFolder\\1750_1799\\1705_1709/PierreGillet1708N.jpg"));
		
		mediaSet.addMedia(Paths.get("C:\\ForTests\\org.fl.gedcomtools\\mediaTestFolder\\1750_1799\\1715_1719/CatherineNicoleFleury1719N.jpg"));
		mediaSet.addMedia(Paths.get("C:\\ForTests\\org.fl.gedcomtools\\mediaTestFolder\\1750_1799\\1715_1719/JeanneNicolleFleury1718N.jpg"));
		
		mediaSet.addMedia(Paths.get("C:\\ForTests\\org.fl.gedcomtools\\mediaTestFolder\\1800_1850\\1840_1844/JeanBaptisteAntoineTaine1841D.jpg"));
		mediaSet.addMedia(Paths.get("C:\\ForTests\\org.fl.gedcomtools\\mediaTestFolder\\1800_1850\\1840_1844/MargueriteIrmaSatabin1840N.jpg"));
		
		List<Path> extraPath = mediaSet.getUnreferencedMedias(rootPath);
		assertThat(extraPath).isNotNull().isEmpty();
	}
	
	@Test
	void shouldReturnTwoUnreferencedMedia() {
		
		MediaSet mediaSet = new MediaSet();
		
		Path rootPath = Paths.get("C:\\ForTests\\org.fl.gedcomtools\\mediaTestFolder");
		mediaSet.addMedia(Paths.get("C:\\ForTests\\org.fl.gedcomtools\\mediaTestFolder\\1750_1799\\1705_1709/AntoinetteGillet1707N.jpg"));
		mediaSet.addMedia(Paths.get("C:\\ForTests\\org.fl.gedcomtools\\mediaTestFolder\\1750_1799\\1705_1709/MathieuGuerinMarieBeuzeville1705M.jpg"));
		
		mediaSet.addMedia(Paths.get("C:\\ForTests\\org.fl.gedcomtools\\mediaTestFolder\\1750_1799\\1715_1719/CatherineNicoleFleury1719N.jpg"));
		mediaSet.addMedia(Paths.get("C:\\ForTests\\org.fl.gedcomtools\\mediaTestFolder\\1750_1799\\1715_1719/JeanneNicolleFleury1718N.jpg"));
		
		mediaSet.addMedia(Paths.get("C:\\ForTests\\org.fl.gedcomtools\\mediaTestFolder\\1800_1850\\1840_1844/JeanBaptisteAntoineTaine1841D.jpg"));
		
		List<Path> extraPath = mediaSet.getUnreferencedMedias(rootPath);
		assertThat(extraPath).isNotNull().hasSize(2).satisfiesExactlyInAnyOrder(
					path -> assertThat(path.getFileName()).hasFileName("MargueriteIrmaSatabin1840N.jpg"),
					path -> assertThat(path.getFileName()).hasFileName("PierreGillet1708N.jpg")
				);
	}
}
