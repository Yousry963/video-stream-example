package com.example.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class VideoService {

	public static final int BYTE_RANGE = 128;

	public ResponseEntity<byte[]> getVideo(String fileName, String range) {

		String VIDEO_PATH = FileSystems.getDefault().getPath("").toAbsolutePath().getParent() + "\\video\\";

		long rangeStart = 0;
		long rangeEnd;
		byte[] data;
		Long fileSize;
		String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
		try {
			Path path = new File(VIDEO_PATH + fileName).toPath();
			fileSize = Optional.ofNullable(fileName).map(file -> path).map(this::sizeFromFile).orElse(0L);
			if (range == null) {
				return ResponseEntity.status(HttpStatus.OK).header("Content-Type", "video" + "/" + fileType).header("Content-Length", String.valueOf(fileSize))
						.body(readByteRange(path, fileName, rangeStart, fileSize - 1));
			}
			String[] ranges = range.split("-");
			rangeStart = Long.parseLong(ranges[0].substring(6));
			if (ranges.length > 1) {
				rangeEnd = Long.parseLong(ranges[1]);
			} else {
				rangeEnd = fileSize - 1;
			}
			if (fileSize < rangeEnd) {
				rangeEnd = fileSize - 1;
			}
			data = readByteRange(path, fileName, rangeStart, rangeEnd);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
		return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).header("Content-Type", "video" + "/" + fileType).header("Accept-Ranges", "bytes").header("Content-Length", contentLength)
				.header("Content-Range", "bytes" + " " + rangeStart + "-" + rangeEnd + "/" + fileSize).body(data);
	}

	public byte[] readByteRange(Path path, String filename, long start, long end) throws IOException {

		try (InputStream inputStream = (Files.newInputStream(path)); ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream()) {
			byte[] data = new byte[BYTE_RANGE];
			int nRead;
			while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
				bufferedOutputStream.write(data, 0, nRead);
			}
			bufferedOutputStream.flush();
			byte[] result = new byte[(int) (end - start) + 1];
			System.arraycopy(bufferedOutputStream.toByteArray(), (int) start, result, 0, result.length);
			return result;
		}
	}

	private Long sizeFromFile(Path path) {
		try {
			return Files.size(path);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return 0L;
	}

}
