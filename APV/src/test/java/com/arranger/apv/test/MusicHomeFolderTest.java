package com.arranger.apv.test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.arranger.apv.util.FileHelper;

public class MusicHomeFolderTest extends ConfigBasedTest {

	private static final String APV_MUSIC_DIR = "apv.musicDir";

	public MusicHomeFolderTest() {
	}

	@Test
	public void findMp3FromMusicFolder() throws Exception {
		
		Path musicPath = null;
		String musicDir = parent.getConfigurator().getRootConfig().getString(APV_MUSIC_DIR);
		if (musicDir == null || musicDir.isEmpty()) {
			String userHome = FileHelper.HOME_DIR;
			musicPath = new File(userHome).toPath().resolve("Music");
		} else {
			musicPath = new File(musicDir).toPath();
		}
		
		Optional<Path> optional = Files.find(musicPath, 4, (p, bfa) -> { 
			boolean res = p.toString().endsWith(".mp3");
			System.out.println(p.toString() + " -> " + res);
			return res;
		}).findFirst();
		if (optional.isPresent()) {
			System.out.println(optional.get().toString());
		}
	}
}
