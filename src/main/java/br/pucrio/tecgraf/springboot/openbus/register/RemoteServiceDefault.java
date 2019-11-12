package br.pucrio.tecgraf.springboot.openbus.register;

import org.omg.PortableServer.Servant;

public class RemoteServiceDefault implements RemoteService {

	private RemoteApplication remoteApplication;
	private Servant servant;
	private String name;
	private String id;

	public RemoteServiceDefault(RemoteApplication remoteApplication, Servant servant, String name, String id) {
		this.remoteApplication = remoteApplication;
		this.servant = servant;
		this.name = name;
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public RemoteApplication getApplication() {
		return remoteApplication;
	}

	public Servant getServant() {
		return servant;
	}

	@Override
	public String toString() {
		return "RemoteServiceDefault{" +
				"remoteApplication=" + remoteApplication +
				", name='" + name + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RemoteServiceDefault that = (RemoteServiceDefault) o;

		if (!remoteApplication.equals(that.remoteApplication)) return false;
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		int result = remoteApplication.hashCode();
		result = 31 * result + name.hashCode();
		return result;
	}

}