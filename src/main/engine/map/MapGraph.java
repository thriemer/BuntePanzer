package map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.joml.Vector2f;

public class MapGraph {

	public BiMap<Integer, Vector2f> lookUp = new BiMap<>();
	public List<List<Integer>> connectionInfo = new ArrayList<>();

	public final List<Vector2f> getNodesFromTo(Vector2f start, Vector2f end) {
		Vector2f closestNodeToStart = lookUp.getValueSet().stream()
				.min((i, j) -> Float.compare(i.distance(start), j.distance(start))).get();
		Vector2f closestNodeToEnd = lookUp.getValueSet().stream()
				.min((i, j) -> Float.compare(i.distance(end), j.distance(end))).get();
		return getNodesFromTo(lookUp.getKey(closestNodeToStart), lookUp.getKey(closestNodeToEnd));
	}

	public final List<Vector2f> getNodesFromTo(int start, int end) {
		return getPathFromTo(start, end).stream().map(i -> lookUp.get(i)).collect(Collectors.toList());
	}

	public final List<Integer> getPathFromTo(int start, int end) {
		List<Integer> fullpathStart = getFullPathName(start);
		List<Integer> fullpathEnd = getFullPathName(end);
		int duplicateIndex = 0;
		for (int i = 0; i < Math.min(fullpathStart.size(), fullpathEnd.size()); i++) {
			if (fullpathStart.get(i) == fullpathEnd.get(i)) {
				duplicateIndex = i;
			}
		}
		List<Integer> calculatedPath = new ArrayList<>(fullpathStart.subList(duplicateIndex, fullpathStart.size()));
		Collections.reverse(calculatedPath);
		calculatedPath.addAll(fullpathEnd.subList(duplicateIndex, fullpathEnd.size()));
		List<Integer> removedDuplicateNodes = calculatedPath.stream().distinct().collect(Collectors.toList());
		return removedDuplicateNodes;
	}

	public final List<Integer> getFullPathName(int nodeName) {
		for (List<Integer> toEndNode : connectionInfo) {
			if (toEndNode.contains(nodeName)) {
				return toEndNode.subList(0, Math.min(toEndNode.indexOf(nodeName) + 1, toEndNode.size()));
			}
		}
		return new ArrayList<>();
	}

}
