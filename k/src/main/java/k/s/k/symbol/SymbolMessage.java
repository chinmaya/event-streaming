package k.s.k.symbol;

public class SymbolMessage {

	private String identifier;

	private Integer partitionId;

	private String symbolType;

	private String symbol;

	public SymbolMessage() {
	}

	public SymbolMessage(String identifier, Integer partitionId, String symbolType, String symbol) {
		this.identifier = identifier;
		this.partitionId = partitionId;
		this.symbolType = symbolType;
		this.symbol = symbol;
	}

	public String toString() {
		return identifier + ",[" + partitionId + "]," + symbolType + "," + symbol;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Integer getPartitionId() {
		return partitionId;
	}

	public void setPartitionId(Integer partitionId) {
		this.partitionId = partitionId;
	}

	public String getSymbolType() {
		return symbolType;
	}

	public void setSymbolType(String symbolType) {
		this.symbolType = symbolType;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
}
