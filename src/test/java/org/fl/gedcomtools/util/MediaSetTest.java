package org.fl.gedcomtools.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class MediaSetTest {

	private static final Logger log = Logger.getLogger(MediaSetTest.class.getName());
	
	@Test
	void shouldThrowNpeWhenNullRootPath() {
		
		MediaSet mediaSet = new MediaSet(log);
		assertThatNullPointerException().isThrownBy(() -> mediaSet.getUnreferencedMedias(null));
	}
	
	@Test
	void shouldReturnNullWhenUnexistantRootPath() {
		
		MediaSet mediaSet = new MediaSet(log);
		
		Path rootPath = Paths.get("/does/not/exists");
		List<Path> extraPath = mediaSet.getUnreferencedMedias(rootPath);
		assertThat(extraPath).isNull();
	}
	
	@Test
	void shouldReturnFullListWhenMediaSetIsEmpty() {
		
		MediaSet mediaSet = new MediaSet(log);
		
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
		
		MediaSet mediaSet = new MediaSet(log);
		
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
		
		MediaSet mediaSet = new MediaSet(log);
		
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
		
		MediaSet mediaSet = new MediaSet(log);
		
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
