class GroovyClient {
    public Map<String, List<String>> getProperties() {
        def result = [:]
        result << ["key1": ["val1", "val2"]]
        result << ["key2": "val3"]
        result
    }
}
