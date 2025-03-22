package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * A timetable backed by a local timetable directory
 *
 * @param directory       the path to the timetable directory
 * @param stringTable     the shared string table
 * @param stations        indexed stations
 * @param stationAliases  alternative station names
 * @param platforms       platform data
 * @param routes          routes
 * @param transfers       transfer connections
 */
public record FileTimeTable(
        Path directory,
        List<String> stringTable,
        Stations stations,
        StationAliases stationAliases,
        Platforms platforms,
        Routes routes,
        Transfers transfers
) implements TimeTable {

    /**
     * Loads a FileTimeTable from the given timetable directory.
     *
     * @param directory the path to the timetable directory
     * @return a FileTimeTable instance
     * @throws IOException if reading files fails
     */
    public TimeTable in(Path directory) throws IOException{
        Objects.requireNonNull(directory);

        Path stringsPath = directory.resolve("strings.txt");
        List<String> stringTable = List.copyOf(Files.readAllLines(stringsPath, StandardCharsets.ISO_8859_1));

        ByteBuffer stationsBuffer = mapFile(directory.resolve("stations.bin"));
        ByteBuffer stationAliasesBuffer = mapFile(directory.resolve("station-aliases.bin"));
        ByteBuffer platformsBuffer = mapFile(directory.resolve("platforms.bin"));
        ByteBuffer routesBuffer = mapFile(directory.resolve("routes.bin"));
        ByteBuffer transfersBuffer = mapFile(directory.resolve("transfers.bin"));

        Stations stations = new BufferedStations(stringTable, stationsBuffer);
        StationAliases stationAliases = new BufferedStationAliases(stringTable, stationAliasesBuffer);
        Platforms platforms = new BufferedPlatforms(stringTable, platformsBuffer);
        Routes routes = new BufferedRoutes(stringTable, routesBuffer);
        Transfers transfers = new BufferedTransfers(transfersBuffer);

        return new FileTimeTable(directory, stringTable, stations, stationAliases, platforms, routes, transfers);
    }

    @Override
    public Trips tripsFor(LocalDate date) {
        try {
            Path tripPath = directory.resolve(date.toString()).resolve("trips.bin");
            ByteBuffer tripsBuffer = mapFile(tripPath);
            return new BufferedTrips(stringTable, tripsBuffer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Connections connectionsFor(LocalDate date) {
        try {
            Path connectionsPath = directory.resolve(date.toString()).resolve("connections.bin");
            ByteBuffer connectionsBuffer = mapFile(connectionsPath);

            Path succConnectionsPath = directory.resolve(date.toString()).resolve("connections-succ.bin");
            ByteBuffer succConnectionsBuffer = mapFile(succConnectionsPath);

            return new BufferedConnections(connectionsBuffer, succConnectionsBuffer);
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

    // own method for memory-mapping files
    private static ByteBuffer mapFile(Path path) throws IOException {
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
            return channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
    }
}
