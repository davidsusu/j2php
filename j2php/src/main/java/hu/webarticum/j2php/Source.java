package hu.webarticum.j2php;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class Source {
	
	private URL url;
	
	public Source(File file) {
		this(uriToUrlSafe(file.toURI()));
	}

	public Source(URL url) {
		this.url = url;
	}
	
	private static URL uriToUrlSafe(URI uri) {
		try {
			return uri.toURL();
		} catch (MalformedURLException e) {
			return null;
		}
	}
	
	public InputStream getInputStream() throws IOException {
		return url.openStream();
	}
	
}
